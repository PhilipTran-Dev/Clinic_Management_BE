package Clinic_Management.ClinicalConsultationService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdjustStockRequest {
    @NotNull(message = "Số lượng thay đổi không được để trống")
    private Integer quantityChange;
}