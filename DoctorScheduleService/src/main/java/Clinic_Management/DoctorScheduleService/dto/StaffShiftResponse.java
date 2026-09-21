package Clinic_Management.DoctorScheduleService.dto;

import Clinic_Management.DoctorScheduleService.entity.Role;
import Clinic_Management.DoctorScheduleService.entity.ShiftSession;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class StaffShiftResponse {
    private Long shiftId;
    private Long userId;
    private String staffName;
    private Role role;
    private String assignedLocation;
    private LocalDate shiftDate;
    private ShiftSession session;
}