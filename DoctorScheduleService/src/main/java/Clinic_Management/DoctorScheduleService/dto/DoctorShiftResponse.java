package Clinic_Management.DoctorScheduleService.dto;

import Clinic_Management.DoctorScheduleService.entity.DutyType;
import Clinic_Management.DoctorScheduleService.entity.ShiftSession;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DoctorShiftResponse {
    private Long id;
    private DoctorReference doctor;
    private Long departmentId;
    private String departmentName;
    private LocalDate shiftDate;
    private ShiftSession session;
    private DutyType dutyType;
    private Integer maxPatientsPerSlot;
    private String roomNumber;
}