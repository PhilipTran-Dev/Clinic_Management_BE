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
     * 1. Subcribe caseshift for a doctor in a department on a specific date and session
     */
    @Transactional
    public DoctorShift registerShift(CreateShiftRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Bác sĩ có ID: " + request.getDoctorId()));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Chuyên khoa có ID: " + request.getDepartmentId()));

        DoctorShift shift = DoctorShift.builder()
                .doctor(doctor)
                .department(department)
                .shiftDate(request.getShiftDate())
                .session(request.getSession())
                .dutyType(request.getDutyType())
                .maxPatientsPerSlot(4) // 4 patients per 60-minute slot as per requirement
                .build();

        return shiftRepository.save(shift);
    }

    /**
     * 2. take all available slots for a department on a specific date, considering the number of doctors and their capacity
     */
    @Transactional(readOnly = true)
    public List<TimeSlotResponse> getAvailableSlots(Long departmentId, LocalDate date) {
        //only take the shifts of doctors who are on duty for outpatient service
        List<DoctorShift> outpatientShifts = shiftRepository
                .findByDepartmentIdAndShiftDateAndDutyType(departmentId, date, DutyType.OUTPATIENT);

        if (outpatientShifts.isEmpty()) {
            return Collections.emptyList();
        }

        List<TimeSlotResponse> slots = new ArrayList<>();


        // create slots for each session: morning (7:30 - 11:30) and afternoon (13:00 - 17:00)
        for (ShiftSession session : ShiftSession.values()) {
            //take all list of doctors who are on duty for this session
            List<DoctorShift> shiftsInSession = outpatientShifts.stream()
                    .filter(s -> s.getSession() == session)
                    .toList();

            if (shiftsInSession.isEmpty()) continue;

            //each case shift is divided into 60-minute slots
            LocalTime current = session.getStartTime();
            while (current.isBefore(session.getEndTime())) {
                LocalTime slotStart = current;
                LocalTime slotEnd = current.plusHours(1);

                // totalCapacity = number of doctors in session * maxPatientsPerSlot (which is 4)
                int totalCapacity = shiftsInSession.stream()
                        .mapToInt(DoctorShift::getMaxPatientsPerSlot)
                        .sum();

                // Count the number of patients already booked in this slot
                long bookedCount = slotAssignmentRepository
                        .countByDepartmentIdAndAppointmentDateAndSlotStartTime(departmentId, date, slotStart);

                int availableCapacity = Math.max(0, totalCapacity - (int) bookedCount);

                slots.add(TimeSlotResponse.builder()
                        .startTime(slotStart)
                        .endTime(slotEnd)
                        .totalCapacity(totalCapacity)
                        .bookedCount((int) bookedCount)
                        .availableCapacity(availableCapacity)
                        .isAvailable(availableCapacity > 0)
                        .build());

                current = slotEnd;
            }
        }

        return slots;
    }

    /**
     * 3. algorithm load balancing (Least-Busy) to assign a doctor to a specific slot for a patient
     * insert patient's ticket number, doctorId, departmentId, date, slotStartTime into SlotAssignment table
     */
    @Transactional
    public AssignDoctorResponse assignDoctorToSlot(Long departmentId, LocalDate date, LocalTime slotStartTime, String ticketNumber) {
        // Determine whether the slot is in the morning or afternoon session
        ShiftSession session = slotStartTime.isBefore(LocalTime.of(12, 0)) ? ShiftSession.MORNING : ShiftSession.AFTERNOON;

        List<DoctorShift> activeShifts = shiftRepository
                .findByDepartmentIdAndShiftDateAndDutyType(departmentId, date, DutyType.OUTPATIENT)
                .stream()
                .filter(s -> s.getSession() == session)
                .toList();

        if (activeShifts.isEmpty()) {
            throw new IllegalStateException("Không có bác sĩ trực phòng khám trong khung giờ này.");
        }

        // find the doctor with the least number of patients assigned in this slot
        Doctor selectedDoctor = null;
        long minPatientCount = Long.MAX_VALUE;

        for (DoctorShift shift : activeShifts) {
            Doctor doctor = shift.getDoctor();
            long currentAssigned = slotAssignmentRepository
                    .countByDoctorIdAndAppointmentDateAndSlotStartTime(doctor.getId(), date, slotStartTime);

            // if the current assigned patients are less than the max patients per slot and also less than the minimum patient count found so far, select this doctor
            if (currentAssigned < shift.getMaxPatientsPerSlot() && currentAssigned < minPatientCount) {
                minPatientCount = currentAssigned;
                selectedDoctor = doctor;
            }
        }

        if (selectedDoctor == null) {
            throw new IllegalStateException("Khung giờ này đã kín lịch cho tất cả bác sĩ.");
        }

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyên khoa ID: " + departmentId));

        SlotAssignment assignment = SlotAssignment.builder()
                .ticketNumber(ticketNumber)
                .doctor(selectedDoctor)
                .department(department) // Sửa departmentId thành department
                .appointmentDate(date)
                .slotStartTime(slotStartTime)
                .build();
        slotAssignmentRepository.save(assignment);

        return AssignDoctorResponse.builder()
                .ticketNumber(ticketNumber)
                .doctorId(selectedDoctor.getId())
                .doctorName(selectedDoctor.getFullName())
                .roomNumber(selectedDoctor.getRoomNumber())
                .date(date)
                .slotStartTime(slotStartTime)
                .message("Phân bổ bác sĩ thành công theo cơ chế cân bằng tải.")
                .build();
    }

    @Transactional
    public StaffShiftResponse registerStaffShift(CreateStaffShiftRequest request) {
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
     * 5. Retrieve scheduled shifts for staff on a specific date
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

    @Transactional(readOnly = true)
    public boolean validateDepartmentRoster(Long departmentId, LocalDate shiftDate, ShiftSession session) {
        long outpatientCount = shiftRepository.countByDepartmentIdAndShiftDateAndSessionAndDutyType(
                departmentId, shiftDate, session, DutyType.OUTPATIENT);
        long inpatientCount = shiftRepository.countByDepartmentIdAndShiftDateAndSessionAndDutyType(
                departmentId, shiftDate, session, DutyType.INPATIENT);

        return outpatientCount >= 1 && inpatientCount >= 1;
    }
}