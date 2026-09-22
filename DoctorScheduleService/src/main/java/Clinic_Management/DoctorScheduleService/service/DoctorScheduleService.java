package Clinic_Management.DoctorScheduleService.service;

import Clinic_Management.DoctorScheduleService.dto.*;
import Clinic_Management.DoctorScheduleService.entity.*;
import Clinic_Management.DoctorScheduleService.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {

    private final DoctorShiftRepository shiftRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final SlotAssignmentRepository slotAssignmentRepository;
    private final UserRepository userRepository;
    private final StaffShiftRepository staffShiftRepository;

    /**
     * 1. Register a duty shift for a Doctor (Outpatient or Inpatient).
     * Validates that the doctor belongs to the department and the shift date is valid.
     */
    @Transactional
    public DoctorShift registerShift(CreateShiftRequest request) {
        if (request.getShiftDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Shift date cannot be in the past.");
        }

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + request.getDoctorId()));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + request.getDepartmentId()));

        // Validate that the doctor belongs to the assigned department
        if (doctor.getDepartment() == null || !doctor.getDepartment().getId().equals(department.getId())) {
            throw new IllegalArgumentException("Doctor " + doctor.getFullName() + " does not belong to department " + department.getName());
        }

        DoctorShift shift = DoctorShift.builder()
                .doctor(doctor)
                .department(department)
                .shiftDate(request.getShiftDate())
                .session(request.getSession())
                .dutyType(request.getDutyType())
                .maxPatientsPerSlot(4) // Default: 4 patients per 60-minute block[cite: 1, 2]
                .build();

        return shiftRepository.save(shift);
    }

    /**
     * 2. Retrieve available 60-minute time slots for a department on a given date.
     * Constraint: Slots are only open if the shift meets compliance (at least 1 outpatient + 1 inpatient doctor)[cite: 1],
     * and slots that have already elapsed today are automatically locked.
     */
    @Transactional(readOnly = true)
    public List<TimeSlotResponse> getAvailableSlots(Long departmentId, LocalDate date) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new IllegalArgumentException("Department not found with ID: " + departmentId);
        }

        if (date.isBefore(LocalDate.now())) {
            return Collections.emptyList();
        }

        List<DoctorShift> outpatientShifts = shiftRepository
                .findByDepartmentIdAndShiftDateAndDutyType(departmentId, date, DutyType.OUTPATIENT);

        if (outpatientShifts.isEmpty()) {
            return Collections.emptyList();
        }

        List<TimeSlotResponse> slots = new ArrayList<>();
        LocalTime nowTime = LocalTime.now();
        boolean isToday = date.isEqual(LocalDate.now());

        for (ShiftSession session : ShiftSession.values()) {
            // Roster compliance validation: Requires at least 1 outpatient and 1 inpatient doctor to accept patients[cite: 1]
            if (!validateDepartmentRoster(departmentId, date, session)) {
                continue;
            }

            List<DoctorShift> shiftsInSession = outpatientShifts.stream()
                    .filter(s -> s.getSession() == session)
                    .toList();

            if (shiftsInSession.isEmpty()) {
                continue;
            }

            // Compute total capacity once per session instead of recalculating inside the slot loop
            int totalCapacity = shiftsInSession.stream()
                    .mapToInt(DoctorShift::getMaxPatientsPerSlot)
                    .sum();

            LocalTime current = session.getStartTime();
            while (current.isBefore(session.getEndTime())) {
                LocalTime slotStart = current;
                LocalTime slotEnd = current.plusHours(1);

                long bookedCount = slotAssignmentRepository
                        .countByDepartmentIdAndAppointmentDateAndSlotStartTime(departmentId, date, slotStart);

                int availableCapacity = Math.max(0, totalCapacity - (int) bookedCount);

                // Lock slot if the start time has already passed today
                boolean isPastSlot = isToday && slotStart.isBefore(nowTime);
                boolean isAvailable = availableCapacity > 0 && !isPastSlot;

                slots.add(TimeSlotResponse.builder()
                        .startTime(slotStart)
                        .endTime(slotEnd)
                        .totalCapacity(totalCapacity)
                        .bookedCount((int) bookedCount)
                        .availableCapacity(isPastSlot ? 0 : availableCapacity)
                        .isAvailable(isAvailable)
                        .build());

                current = slotEnd;
            }
        }

        return slots;
    }

    /**
     * 3. Assign a doctor to a patient slot using Least-Busy load balancing[cite: 1, 2].
     * Validates that the appointment date/time is not in the past, the shift roster is compliant[cite: 1],
     * and slotStartTime adheres to standard 60-minute blocks[cite: 1].
     */
    @Transactional
    public AssignDoctorResponse assignDoctorToSlot(Long departmentId, LocalDate date, LocalTime slotStartTime, String ticketNumber) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot schedule appointments in the past.");
        }

        if (date.isEqual(LocalDate.now()) && slotStartTime.isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("Time slot " + slotStartTime + " has already passed for today.");
        }

        // Validate 60-minute slot boundaries and determine the shift session (Morning / Afternoon)[cite: 1, 2]
        ShiftSession session = determineAndValidateSession(slotStartTime);

        // Validate minimum roster requirements before assigning
        if (!validateDepartmentRoster(departmentId, date, session)) {
            throw new IllegalStateException("Department shift is not compliant to accept patients (Requires at least 1 Outpatient and 1 Inpatient doctor).");
        }

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + departmentId));

        List<DoctorShift> activeShifts = shiftRepository
                .findByDepartmentIdAndShiftDateAndDutyType(departmentId, date, DutyType.OUTPATIENT)
                .stream()
                .filter(s -> s.getSession() == session)
                .toList();

        if (activeShifts.isEmpty()) {
            throw new IllegalStateException("No outpatient doctors on duty for this session.");
        }

        // Least-Busy load balancing algorithm: Select doctor with the fewest assigned patients in this slot[cite: 1, 2]
        Doctor selectedDoctor = null;
        long minPatientCount = Long.MAX_VALUE;

        for (DoctorShift shift : activeShifts) {
            Doctor doctor = shift.getDoctor();
            long currentAssigned = slotAssignmentRepository
                    .countByDoctorIdAndAppointmentDateAndSlotStartTime(doctor.getId(), date, slotStartTime);

            if (currentAssigned < shift.getMaxPatientsPerSlot() && currentAssigned < minPatientCount) {
                minPatientCount = currentAssigned;
                selectedDoctor = doctor;
            }
        }

        if (selectedDoctor == null) {
            throw new IllegalStateException("Time slot " + slotStartTime + " is fully booked for all on-duty doctors.");
        }

        SlotAssignment assignment = SlotAssignment.builder()
                .ticketNumber(ticketNumber)
                .doctor(selectedDoctor)
                .department(department)
                .appointmentDate(date)
                .slotStartTime(slotStartTime)
                .status(AssignmentStatus.BOOKED)
                .build();
        slotAssignmentRepository.save(assignment);

        return AssignDoctorResponse.builder()
                .ticketNumber(ticketNumber)
                .doctorId(selectedDoctor.getId())
                .doctorName(selectedDoctor.getFullName())
                .roomNumber(selectedDoctor.getRoomNumber())
                .date(date)
                .slotStartTime(slotStartTime)
                .message("Doctor assigned successfully via Least-Busy load balancing.")
                .build();
    }

    /**
     * 4. Register a duty shift for Nurse or Pharmacist personnel.
     */
    @Transactional
    public StaffShiftResponse registerStaffShift(CreateStaffShiftRequest request) {
        if (request.getShiftDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Shift date cannot be in the past.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.getUserId()));

        if (user.getRole() != Role.NURSE && user.getRole() != Role.PHARMACIST) {
            throw new IllegalArgumentException("Station shifts are only applicable for NURSE or PHARMACIST roles.");
        }

        StaffShift shift = StaffShift.builder()
                .user(user)
                .staffName(user.getFullName())
                .role(user.getRole())
                .assignedLocation(request.getAssignedLocation())
                .shiftDate(request.getShiftDate())
                .session(request.getSession())
                .build();

        StaffShift saved = staffShiftRepository.save(shift);

        return StaffShiftResponse.builder()
                .shiftId(saved.getId())
                .userId(user.getId())
                .staffName(saved.getStaffName())
                .role(saved.getRole())
                .assignedLocation(saved.getAssignedLocation())
                .shiftDate(saved.getShiftDate())
                .session(saved.getSession())
                .build();
    }

    /**
     * 5. Retrieve scheduled staff shifts for a specific date.
     */
    @Transactional(readOnly = true)
    public List<StaffShiftResponse> getStaffShifts(LocalDate date, Role role) {
        List<StaffShift> shifts = (role == null)
                ? staffShiftRepository.findByShiftDate(date)
                : staffShiftRepository.findByShiftDateAndRole(date, role);

        return shifts.stream()
                .map(s -> StaffShiftResponse.builder()
                        .shiftId(s.getId())
                        .userId(s.getUser().getId())
                        .staffName(s.getStaffName())
                        .role(s.getRole())
                        .assignedLocation(s.getAssignedLocation())
                        .shiftDate(s.getShiftDate())
                        .session(s.getSession())
                        .build())
                .toList();
    }

    /**
     * 6. Validate department roster compliance:
     * Each specialty during an active shift must have at least 1 outpatient doctor and 1 inpatient doctor[cite: 1].
     */
    @Transactional(readOnly = true)
    public boolean validateDepartmentRoster(Long departmentId, LocalDate shiftDate, ShiftSession session) {
        long outpatientCount = shiftRepository.countByDepartmentIdAndShiftDateAndSessionAndDutyType(
                departmentId, shiftDate, session, DutyType.OUTPATIENT);
        long inpatientCount = shiftRepository.countByDepartmentIdAndShiftDateAndSessionAndDutyType(
                departmentId, shiftDate, session, DutyType.INPATIENT);

        return outpatientCount >= 1 && inpatientCount >= 1;
    }

    /**
     * Helper method to validate if the given time matches standard 60-minute block boundaries:
     * - Morning: 07:30, 08:30, 09:30, 10:30[cite: 2]
     * - Afternoon: 13:00, 14:00, 15:00, 16:00[cite: 2]
     */
    private ShiftSession determineAndValidateSession(LocalTime time) {
        List<LocalTime> morningSlots = List.of(
                LocalTime.of(7, 30), LocalTime.of(8, 30), LocalTime.of(9, 30), LocalTime.of(10, 30)
        );
        List<LocalTime> afternoonSlots = List.of(
                LocalTime.of(13, 0), LocalTime.of(14, 0), LocalTime.of(15, 0), LocalTime.of(16, 0)
        );

        if (morningSlots.contains(time)) {
            return ShiftSession.MORNING;
        } else if (afternoonSlots.contains(time)) {
            return ShiftSession.AFTERNOON;
        } else {
            throw new IllegalArgumentException("Invalid slot start time. Times must strictly align with 60-minute blocks: " +
                    "Morning (07:30, 08:30, 09:30, 10:30) or Afternoon (13:00, 14:00, 15:00, 16:00).");
        }
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public List<Doctor> getDoctorsByDepartment(Long departmentId) {
        return doctorRepository.findByDepartmentIdAndActiveTrue(departmentId);
    }

    public List<DoctorShift> getShiftsByDepartmentAndDate(Long departmentId, LocalDate date) {
        return shiftRepository.findByDepartmentIdAndShiftDate(departmentId, date);
    }
}