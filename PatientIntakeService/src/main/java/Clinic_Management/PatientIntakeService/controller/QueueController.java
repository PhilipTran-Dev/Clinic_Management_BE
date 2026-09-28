package Clinic_Management.PatientIntakeService.controller;
import Clinic_Management.PatientIntakeService.entity.QueueStatus;
import Clinic_Management.PatientIntakeService.entity.QueueTicket;
import Clinic_Management.PatientIntakeService.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
@Tag(name = "Queue Management API", description = "Quản lý và điều phối hàng đợi khám bệnh")
public class QueueController {

    private final QueueService queueService;

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Lấy danh sách hàng đợi đang chờ của Bác sĩ (theo độ ưu tiên P1 -> P2 -> P3)")
    public ResponseEntity<List<QueueTicket>> getDoctorQueue(
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(queueService.getDoctorActiveQueue(doctorId, targetDate));
    }

    @PostMapping("/doctor/{doctorId}/call-next")
    @Operation(summary = "Bác sĩ gọi bệnh nhân kế tiếp vào phòng")
    public ResponseEntity<QueueTicket> callNext(
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(queueService.callNextPatient(doctorId, targetDate));
    }

    @PatchMapping("/tickets/{ticketNumber}/status")
    @Operation(summary = "Cập nhật trạng thái phiếu (SKIPPED, COMPLETED, CANCELLED)")
    public ResponseEntity<QueueTicket> updateStatus(
            @PathVariable String ticketNumber,
            @RequestParam QueueStatus status) {
        return ResponseEntity.ok(queueService.updateTicketStatus(ticketNumber, status));
    }
}