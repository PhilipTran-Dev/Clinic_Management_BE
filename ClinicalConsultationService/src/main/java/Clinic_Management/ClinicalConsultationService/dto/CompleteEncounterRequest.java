package Clinic_Management.ClinicalConsultationService.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CompleteEncounterRequest {
    private String subjective;
    private String objective;
    private String assessment;
    private String plan;

    private String bloodPressure;
    private Integer heartRate;
    private String temperature;
    private Integer spo2;

    @NotEmpty(message = "Ca khám phải có tối thiểu 1 chẩn đoán ICD-10")
    @Valid
    private List<DiagnosisRequest> diagnoses;

    @Valid
    private List<PrescriptionItemRequest> prescriptionItems;

    private List<String> patientAllergies;
}