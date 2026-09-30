package Clinic_Management.ClinicalConsultationService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescriptions", indexes = {
        @Index(name = "idx_prescription_ticket", columnList = "ticket_number"),
        @Index(name = "idx_prescription_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "encounter_id", nullable = false)
    private Long encounterId;

    @Column(name = "ticket_number", nullable = false, length = 20)
    private String ticketNumber;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "patient_name", nullable = false)
    private String patientName;

    private String insuranceCode;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "doctor_name", nullable = false)
    private String doctorName;

    private Long pharmacistId; // Ghi nhận dược sĩ xuất kho[cite: 5]
    private String pharmacistName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PrescriptionStatus status = PrescriptionStatus.PENDING_DISPENSE;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalAmount; // Tổng tiền đơn thuốc[cite: 5]

    @Column(precision = 12, scale = 2)
    private BigDecimal insurancePaidAmount; // BHYT chi trả[cite: 5]

    @Column(precision = 12, scale = 2)
    private BigDecimal patientCopayAmount; // Bệnh nhân chi trả[cite: 5]

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PrescriptionItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime dispensedAt;

    public void addItem(PrescriptionItem item) {
        items.add(item);
        item.setPrescription(this);
    }
}