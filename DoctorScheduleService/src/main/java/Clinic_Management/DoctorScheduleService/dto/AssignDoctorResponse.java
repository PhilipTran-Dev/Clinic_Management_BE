package Clinic_Management.DoctorScheduleService.dto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class AssignDoctorResponse {
    private String ticketNumber;
    private Long doctorId;
    private String doctorName;
    private String roomNumber;
    private LocalDate date;
    private LocalTime slotStartTime; //time slot start time
    private String message;
}