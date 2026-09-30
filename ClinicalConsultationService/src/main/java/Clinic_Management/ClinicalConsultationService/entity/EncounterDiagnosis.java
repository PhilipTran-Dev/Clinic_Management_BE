package Clinic_Management.ClinicalConsultationService.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "encounter_diagnoses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncounterDiagnosis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    private Encounter encounter;

    @Column(name = "icd10_code", nullable = false, length = 15)
    private String icd10Code;

    @Column(name = "disease_name", nullable = false)
    private String diseaseName;

    @Builder.Default
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false; // primary diagnosis or secondary diagnosis

    private Integer aiConfidence; // Confidence score from AI model, if applicable
}