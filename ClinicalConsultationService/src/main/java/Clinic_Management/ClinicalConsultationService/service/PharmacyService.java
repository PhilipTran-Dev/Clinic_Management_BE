package Clinic_Management.ClinicalConsultationService.service;

import Clinic_Management.ClinicalConsultationService.dto.PrescriptionDetailResponse;
import Clinic_Management.ClinicalConsultationService.entity.Drug;
import Clinic_Management.ClinicalConsultationService.entity.Prescription;
import Clinic_Management.ClinicalConsultationService.entity.PrescriptionItem;
import Clinic_Management.ClinicalConsultationService.entity.PrescriptionStatus;
import Clinic_Management.ClinicalConsultationService.repository.DrugRepository;
import Clinic_Management.ClinicalConsultationService.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PharmacyService {

    private final PrescriptionRepository prescriptionRepository;
    private final DrugRepository drugRepository;


    @Transactional(readOnly = true)
    public List<PrescriptionDetailResponse> getPrescriptionsByStatus(PrescriptionStatus status) {
        return prescriptionRepository.findQueueByStatus(status).stream()
                .map(this::mapToDetailResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public PrescriptionDetailResponse getPrescriptionDetails(Long prescriptionId) {
        Prescription p = prescriptionRepository.findByIdWithItems(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn thuốc ID: " + prescriptionId));
        return mapToDetailResponse(p);
    }

    @Transactional
    public PrescriptionDetailResponse dispensePrescription(Long prescriptionId, Long pharmacistId, String pharmacistName) {
        Prescription prescription = prescriptionRepository.findByIdWithItems(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn thuốc ID: " + prescriptionId));

        if (prescription.getStatus() == PrescriptionStatus.DISPENSED) {
            throw new IllegalStateException("Đơn thuốc này đã được xuất kho và phát thành công trước đó.");
        }

        for (PrescriptionItem item : prescription.getItems()) {
            Drug drug = item.getDrug();
            if (drug.getStockQuantity() < item.getQuantity()) {
                throw new IllegalStateException(String.format(
                        "Thuốc %s (Mã %s) không đủ tồn kho để xuất. Tồn kho: %d, Cần xuất: %d",
                        drug.getName(), drug.getCode(), drug.getStockQuantity(), item.getQuantity()
                ));
            }
            drug.setStockQuantity(drug.getStockQuantity() - item.getQuantity());
            drugRepository.save(drug);
        }

        prescription.setStatus(PrescriptionStatus.DISPENSED);
        prescription.setPharmacistId(pharmacistId);
        prescription.setPharmacistName(pharmacistName);
        prescription.setDispensedAt(LocalDateTime.now());

        Prescription saved = prescriptionRepository.save(prescription);
        log.info("Dược sĩ {} đã phát thành công đơn thuốc #{}, thu viện phí: {} đ",
                pharmacistName, saved.getId(), saved.getPatientCopayAmount());

        return mapToDetailResponse(saved);
    }


    @Transactional(readOnly = true)
    public List<Drug> getActiveDrugs() {
        return drugRepository.findByActiveTrueOrderByNameAsc();
    }

    private PrescriptionDetailResponse mapToDetailResponse(Prescription p) {
        List<PrescriptionDetailResponse.PrescriptionItemResponse> itemResponses = p.getItems().stream()
                .map(i -> PrescriptionDetailResponse.PrescriptionItemResponse.builder()
                        .itemId(i.getId())
                        .drugId(i.getDrug().getId())
                        .drugCode(i.getDrug().getCode())
                        .drugName(i.getDrugName())
                        .concentration(i.getDrug().getConcentration())
                        .dosageForm(i.getDosageForm())
                        .routeFrequency(i.getRouteFrequency())
                        .duration(i.getDuration())
                        .quantity(i.getQuantity())
                        .currentStock(i.getDrug().getStockQuantity())
                        .unitPrice(i.getUnitPrice())
                        .amount(i.getAmount())
                        .build())
                .toList();

        return PrescriptionDetailResponse.builder()
                .prescriptionId(p.getId())
                .encounterId(p.getEncounterId())
                .ticketNumber(p.getTicketNumber())
                .patientId(p.getPatientId())
                .patientName(p.getPatientName())
                .insuranceCode(p.getInsuranceCode())
                .doctorId(p.getDoctorId())
                .doctorName(p.getDoctorName())
                .pharmacistId(p.getPharmacistId())
                .pharmacistName(p.getPharmacistName())
                .status(p.getStatus())
                .totalAmount(p.getTotalAmount())
                .insurancePaidAmount(p.getInsurancePaidAmount())
                .patientCopayAmount(p.getPatientCopayAmount())
                .createdAt(p.getCreatedAt())
                .dispensedAt(p.getDispensedAt())
                .items(itemResponses)
                .build();
    }
}