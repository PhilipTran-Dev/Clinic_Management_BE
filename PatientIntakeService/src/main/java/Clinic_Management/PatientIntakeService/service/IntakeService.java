package Clinic_Management.PatientIntakeService.service;


import Clinic_Management.PatientIntakeService.clients.ScheduleServiceClient;
import Clinic_Management.PatientIntakeService.dto.AdministrativeIntakeRequest;
import Clinic_Management.PatientIntakeService.dto.IntakeResponse;
import Clinic_Management.PatientIntakeService.dto.ScheduleAssignDoctorRequest;
import Clinic_Management.PatientIntakeService.dto.ScheduleAssignDoctorResponse;
import Clinic_Management.PatientIntakeService.entity.Patient;
import Clinic_Management.PatientIntakeService.entity.QueueStatus;
import Clinic_Management.PatientIntakeService.entity.QueueTicket;
import Clinic_Management.PatientIntakeService.repository.PatientRepository;
import Clinic_Management.PatientIntakeService.repository.QueueTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntakeService {

    private final PatientRepository patientRepository;
    private final QueueTicketRepository queueTicketRepository;
    private final ScheduleServiceClient scheduleClient;

    @Transactional
    public IntakeResponse processIntake(AdministrativeIntakeRequest request) {
        Patient patient = resolvePatient(request);

        String ticketNumber = generateTicketNumber(request.getDepartmentId(), request.getAppointmentDate());

        ScheduleAssignDoctorResponse doctorAssignment = scheduleClient.assignDoctor(
                ScheduleAssignDoctorRequest.builder()
                        .departmentId(request.getDepartmentId())
                        .appointmentDate(request.getAppointmentDate())
                        .slotStartTime(request.getSlotStartTime())
                        .ticketNumber(ticketNumber)
                        .patientName(patient.getFullName())
                        .patientPhone(patient.getPhone())
                        .insuranceCode(patient.getInsuranceCode())
                        .chiefComplaint(request.getChiefComplaint())
                        .build()
        );

        QueueTicket ticket = QueueTicket.builder()
                .ticketNumber(ticketNumber)
                .patient(patient)
                .departmentId(request.getDepartmentId())
                .departmentName(request.getDepartmentName())
                .doctorId(doctorAssignment.getDoctorId())
                .doctorName(doctorAssignment.getDoctorName())
                .roomNumber(doctorAssignment.getRoomNumber())
                .appointmentDate(request.getAppointmentDate())
                .slotStartTime(request.getSlotStartTime())
                .priorityLevel(request.getPriorityLevel())
                .intakeSource(request.getIntakeSource())
                .status(QueueStatus.WAITING)
                .chiefComplaint(request.getChiefComplaint())
                .build();

        queueTicketRepository.save(ticket);

        log.info("Bệnh nhân {} đã được tiếp nhận thành công. Phiếu: {}, Phòng: {}",
                patient.getFullName(), ticketNumber, doctorAssignment.getRoomNumber());

        return IntakeResponse.builder()
                .ticketNumber(ticket.getTicketNumber())
                .patientId(patient.getId())
                .patientName(patient.getFullName())
                .insuranceCode(patient.getInsuranceCode())
                .departmentId(ticket.getDepartmentId())
                .departmentName(ticket.getDepartmentName())
                .doctorId(ticket.getDoctorId())
                .doctorName(ticket.getDoctorName())
                .roomNumber(ticket.getRoomNumber())
                .appointmentDate(ticket.getAppointmentDate())
                .slotStartTime(ticket.getSlotStartTime())
                .priorityLevel(ticket.getPriorityLevel())
                .checkInTime(ticket.getCheckInTime())
                .message("Tiếp nhận thành công. Vui lòng di chuyển đến phòng khám.")
                .build();
    }

    private Patient resolvePatient(AdministrativeIntakeRequest req) {
        if (req.getIdentityCardNumber() != null && !req.getIdentityCardNumber().isBlank()) {
            return patientRepository.findByIdentityCardNumber(req.getIdentityCardNumber())
                    .map(existing -> updatePatientDetails(existing, req))
                    .orElseGet(() -> createPatient(req));
        }
        if (req.getInsuranceCode() != null && !req.getInsuranceCode().isBlank()) {
            return patientRepository.findByInsuranceCode(req.getInsuranceCode())
                    .map(existing -> updatePatientDetails(existing, req))
                    .orElseGet(() -> createPatient(req));
        }
        return createPatient(req);
    }

    private Patient createPatient(AdministrativeIntakeRequest req) {
        Patient patient = Patient.builder()
                .fullName(req.getFullName().trim())
                .identityCardNumber(req.getIdentityCardNumber())
                .insuranceCode(req.getInsuranceCode())
                .initialHospitalCode(req.getInitialHospitalCode())
                .dateOfBirth(req.getDateOfBirth())
                .gender(req.getGender())
                .phone(req.getPhone())
                .address(req.getAddress())
                .isOcrVerified(Boolean.TRUE.equals(req.getIsOcrVerified()))
                .build();
        return patientRepository.save(patient);
    }

    private Patient updatePatientDetails(Patient patient, AdministrativeIntakeRequest req) {
        patient.setFullName(req.getFullName().trim());
        if (req.getPhone() != null) patient.setPhone(req.getPhone());
        if (req.getAddress() != null) patient.setAddress(req.getAddress());
        if (req.getInitialHospitalCode() != null) patient.setInitialHospitalCode(req.getInitialHospitalCode());
        if (Boolean.TRUE.equals(req.getIsOcrVerified())) patient.setIsOcrVerified(true);
        return patientRepository.save(patient);
    }

    private String generateTicketNumber(Long departmentId, LocalDate date) {
        long currentCount = queueTicketRepository.countByAppointmentDateAndDepartmentId(date, departmentId) + 1;
        String prefix = switch (departmentId.intValue()) {
            case 1 -> "A"; // Khoa Nội Tổng quát & Tim mạch
            case 2 -> "B"; // Khoa Hô hấp & Dị ứng
            case 3 -> "C"; // Khoa Da liễu
            default -> "K";
        };
        return String.format("#%s-%03d", prefix, currentCount);
    }
}