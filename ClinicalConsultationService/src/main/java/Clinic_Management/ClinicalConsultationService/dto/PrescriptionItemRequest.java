package Clinic_Management.ClinicalConsultationService.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrescriptionItemRequest {
    @NotNull(message = "ID thuốc không được để trống")
    private Long drugId;

    @NotNull(message = "Số lượng kê không được để trống")
    @Min(value = 1, message = "Số lượng phải tối thiểu là 1")
    private Integer quantity;

    private String routeFrequency;
    private String duration;
}