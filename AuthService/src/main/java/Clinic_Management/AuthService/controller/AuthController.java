package Clinic_Management.AuthService.controller;

import Clinic_Management.AuthService.dto.*;
import Clinic_Management.AuthService.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication & User API", description = "Xác thực tập trung, Cấp phát JWT và Quản lý tài khoản")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập hệ thống (Xác thực Email/Password, trả về JWT Token và User Context)")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Bệnh nhân tự tạo tài khoản cá nhân trực tuyến")
    public ResponseEntity<AuthResponse> registerPatient(@Valid @RequestBody RegisterPatientRequest request) {
        return ResponseEntity.ok(authService.registerPatient(request));
    }

    @PostMapping("/admin/users")
    @Operation(summary = "Quản trị viên cấp tài khoản mới cho Bác sĩ, Điều dưỡng, Lễ tân, Dược sĩ")
    public ResponseEntity<AuthResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.ok(authService.createStaffAccount(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Lấy thông tin người dùng từ JWT Token trong Header Authorization")
    public ResponseEntity<AuthResponse> getMyProfile(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(authService.getProfileByToken(authHeader));
    }
    @GetMapping("/admin/patients")
    @Operation(summary = "Admin lấy danh sách toàn bộ Bệnh nhân kèm thông tin BHYT")
    public ResponseEntity<List<AuthResponse>> getAllPatients() {
        return ResponseEntity.ok(authService.getAllPatients());
    }

    @PutMapping("/admin/patients/{id}")
    @Operation(summary = "Admin cập nhật thông tin định danh và BHYT của Bệnh nhân")
    public ResponseEntity<AuthResponse> updatePatientByAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientRequest request) {
        return ResponseEntity.ok(authService.updatePatientByAdmin(id, request));
    }

    @PutMapping("/me/profile")
    @Operation(summary = "Người dùng tự cập nhật hồ sơ cá nhân và BHYT")
    public ResponseEntity<AuthResponse> updateMyProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdatePatientRequest request) {
        return ResponseEntity.ok(authService.updateMyProfile(authHeader, request));
    }
}