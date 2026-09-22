package Clinic_Management.DoctorScheduleService.controller;

import Clinic_Management.DoctorScheduleService.dto.*;
import Clinic_Management.DoctorScheduleService.entity.*;
import Clinic_Management.DoctorScheduleService.service.DoctorScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
@Tag(name = "Doctor Schedule API", description = "Quản lý lịch trực bác sĩ và slot khám")
public class DoctorScheduleController {

    private final DoctorScheduleService scheduleService;

    @PostMapping("/shifts")
    @Operation(summary = "Admin đăng ký ca trực cho bác sĩ (Ngoại trú hoặc Nội trú)")
    public ResponseEntity<DoctorShift> registerShift(@Valid @RequestBody CreateShiftRequest request) {
        return ResponseEntity.ok(scheduleService.registerShift(request));
    }

    @GetMapping("/available-slots")
    @Operation(summary = "Xem các khung giờ khám 60 phút và số chỗ còn trống")
    public ResponseEntity<List<TimeSlotResponse>> getAvailableSlots(
            @RequestParam Long departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(scheduleService.getAvailableSlots(departmentId, date));
    }

    @PostMapping("/assign-doctor")
    @Operation(summary = "Phân bổ bác sĩ cho bệnh nhân vào slot (Cân bằng tải Least-Busy)")
    public ResponseEntity<AssignDoctorResponse> assignDoctor(
            @RequestParam Long departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime slotStartTime,
            @RequestParam String ticketNumber) {
        return ResponseEntity.ok(scheduleService.assignDoctorToSlot(departmentId, date, slotStartTime, ticketNumber));
    }

    @PostMapping("/staff-shifts")
    @Operation(summary = "Admin assigns shift for Nurse or Pharmacist at specific station/counter")
    public ResponseEntity<StaffShiftResponse> registerStaffShift(@Valid @RequestBody CreateStaffShiftRequest request) {
        return ResponseEntity.ok(scheduleService.registerStaffShift(request));
    }

    @GetMapping("/staff-shifts")
    @Operation(summary = "Get roster of Nurses and Pharmacists on duty by date")
    public ResponseEntity<List<StaffShiftResponse>> getStaffShifts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Role role) {
        return ResponseEntity.ok(scheduleService.getStaffShifts(date, role));
    }

    @GetMapping("/roster/validate")
    @Operation(summary = "Kiểm tra ca trực có đạt chuẩn (tối thiểu 1 BS Ngoại trú và 1 BS Nội trú) hay không")
    public ResponseEntity<Boolean> validateRoster(
            @RequestParam Long departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam ShiftSession session) {
        return ResponseEntity.ok(scheduleService.validateDepartmentRoster(departmentId, date, session));
    }

    @GetMapping("/departments")
    @Operation(summary = "Lấy danh mục tất cả chuyên khoa")
    public ResponseEntity<List<Department>> getDepartments() {
        return ResponseEntity.ok(scheduleService.getAllDepartments());
    }

    @GetMapping("/doctors")
    @Operation(summary = "Lấy danh sách bác sĩ theo chuyên khoa")
    public ResponseEntity<List<Doctor>> getDoctors(@RequestParam Long departmentId) {
        return ResponseEntity.ok(scheduleService.getDoctorsByDepartment(departmentId));
    }


    @GetMapping("/shifts")
    @Operation(summary = "Xem lịch trực bác sĩ theo ngày và chuyên khoa")
    public ResponseEntity<List<DoctorShiftResponse>> getShifts(
            @RequestParam Long departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(scheduleService.getShiftsByDepartmentAndDate(departmentId, date));
    }

    @GetMapping("/shifts/weekly")
    @Operation(summary = "Lấy lịch trực tuần của bác sĩ theo chuyên khoa và khoảng ngày")
    public ResponseEntity<List<DoctorShiftResponse>> getWeeklyShifts(
            @RequestParam Long departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(scheduleService.getWeeklyShifts(departmentId, startDate, endDate));
    }
}