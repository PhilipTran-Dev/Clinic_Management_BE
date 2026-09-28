package Clinic_Management.PatientIntakeService.dto;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleAssignDoctorResponse {
    private String ticketNumber;
    private Long doctorId;
    private String doctorName;
    private String roomNumber;
    private LocalDate date;
    private LocalTime slotStartTime;
    private String message;
}