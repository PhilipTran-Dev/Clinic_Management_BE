package Clinic_Management.PatientIntakeService.entity;

public enum IntakeSource {
    KIOSK_OCR,
    KIOSK_QR,         // scan QR code from Patient Portal
    RECEPTION_MANUAL,
    ONLINE_BOOKING
}