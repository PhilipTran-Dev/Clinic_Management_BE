package Clinic_Management.PatientIntakeService.controller;
import Clinic_Management.PatientIntakeService.clients.ScheduleServiceClient;
import Clinic_Management.PatientIntakeService.dto.AdministrativeIntakeRequest;
import Clinic_Management.PatientIntakeService.dto.IntakeResponse;
import Clinic_Management.PatientIntakeService.dto.ScheduleTimeSlotResponse;
import Clinic_Management.PatientIntakeService.service.IntakeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/intake")
@RequiredArgsConstructor
@Tag(name = "Patient Intake API", description = "Tiếp nhận hành chính, phân chuyên khoa và cấp số thứ tự")
public class IntakeController {

    private final IntakeService intakeService;
    private final ScheduleServiceClient scheduleClient;

    @PostMapping("/check-in")
    @Operation(summary = "Tiếp nhận bệnh nhân (từ Kiosk OCR hoặc nhập tay tại Quầy)")
    public ResponseEntity<IntakeResponse> processIntake(@Valid @RequestBody AdministrativeIntakeRequest request) {
        return ResponseEntity.ok(intakeService.processIntake(request));
    }

    @GetMapping("/available-slots")
    @Operation(summary = "Tra cứu các khung giờ slot khả dụng từ DoctorScheduleService")
    public ResponseEntity<List<ScheduleTimeSlotResponse>> getAvailableSlots(
            @RequestParam Long departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(scheduleClient.getAvailableSlots(departmentId, date));
    }
}