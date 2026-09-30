package Clinic_Management.ClinicalConsultationService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DiagnosisRequest {
    @NotBlank(message = "Mã ICD-10 không được để trống")
    private String icd10Code; // J02.9

    @NotBlank(message = "Tên bệnh không được để trống")
    private String diseaseName;

    private Boolean isPrimary = false;
    private Integer aiConfidence;
}