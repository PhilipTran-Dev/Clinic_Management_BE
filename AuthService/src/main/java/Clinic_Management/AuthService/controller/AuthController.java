package Clinic_Management.AuthService.controller;

import Clinic_Management.AuthService.dto.AuthResponse;
import Clinic_Management.AuthService.dto.CreateStaffRequest;
import Clinic_Management.AuthService.dto.LoginRequest;
import Clinic_Management.AuthService.dto.RegisterPatientRequest;
import Clinic_Management.AuthService.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}