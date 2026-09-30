package Clinic_Management.ClinicalConsultationService.entity;

public enum PrescriptionStatus {
    PENDING_DISPENSE, // doctor accepted prescription, waiting for pharmacist to dispense
    DISPENSING,       // pharmacist is dispensing the prescription
    DISPENSED,        // drug has been dispensed to the patient
    CANCELLED
}