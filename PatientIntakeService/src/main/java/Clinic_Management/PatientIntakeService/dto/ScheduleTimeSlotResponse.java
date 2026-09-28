package Clinic_Management.PatientIntakeService.dto;
import lombok.Data;
import java.time.LocalTime;

@Data
public class ScheduleTimeSlotResponse {
    private LocalTime startTime;
    private LocalTime endTime;
    private int totalCapacity;
    private int bookedCount;
    private int availableCapacity;
    private boolean isAvailable;
}