package Clinic_Management.DoctorScheduleService.dto;

import Clinic_Management.DoctorScheduleService.entity.ShiftSession;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateStaffShiftRequest {
    @NotNull(message = "User ID must not be null")
    private Long userId;

    @NotNull(message = "Shift date must not be null")
    @FutureOrPresent(message = "Shift date must be present or future")
    private LocalDate shiftDate;

    @NotNull(message = "Session must not be null")
    private ShiftSession session;

    @NotBlank(message = "Assigned location must not be blank")
    private String assignedLocation;
}