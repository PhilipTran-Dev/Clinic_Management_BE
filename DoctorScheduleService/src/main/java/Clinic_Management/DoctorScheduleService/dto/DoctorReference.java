package Clinic_Management.DoctorScheduleService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorReference {
    private Long id;
    private String fullName;
    private String title;
    private String roomNumber;
}