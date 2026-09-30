package Clinic_Management.AuthService.service;

import Clinic_Management.AuthService.dto.AuthResponse;
import Clinic_Management.AuthService.dto.CreateStaffRequest;
import Clinic_Management.AuthService.dto.LoginRequest;
import Clinic_Management.AuthService.dto.RegisterPatientRequest;
import Clinic_Management.AuthService.entity.User;
import Clinic_Management.AuthService.entity.UserRole;
import Clinic_Management.AuthService.repository.UserRepository;
import Clinic_Management.AuthService.security.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác"));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new IllegalStateException("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Quản trị viên.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác");
        }

        String token = jwtProvider.generateToken(user);
        log.info("Người dùng {} ({}) đăng nhập thành công", user.getEmail(), user.getRole());

        return mapToAuthResponse(user, token);
    }

    @Transactional
    public AuthResponse registerPatient(RegisterPatientRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email này đã được đăng ký tài khoản trong hệ thống");
        }

        User patient = User.builder()
                .fullName(request.getFullName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(UserRole.PATIENT)
                .active(true)
                .build();

        User saved = userRepository.save(patient);
        String token = jwtProvider.generateToken(saved);

        log.info("Bệnh nhân mới tự đăng ký thành công: {}", email);
        return mapToAuthResponse(saved, token);
    }

    @Transactional
    public AuthResponse createStaffAccount(CreateStaffRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email này đã được sử dụng");
        }

        String defaultPassword = "password123";

        User staff = User.builder()
                .fullName(request.getFullName().trim())
                .email(email)
                .password(passwordEncoder.encode(defaultPassword))
                .phone(request.getPhone())
                .role(request.getRole())
                .departmentId(request.getDepartmentId())
                .departmentName(request.getDepartmentName())
                .doctorId(request.getDoctorId())
                .pharmacistId(request.getPharmacistId())
                .active(true)
                .build();

        User saved = userRepository.save(staff);
        log.info("Quản trị viên đã cấp tài khoản nhân sự mới: {} ({})", email, request.getRole());

        return mapToAuthResponse(saved, null);
    }

    @Transactional(readOnly = true)
    public AuthResponse getProfileByToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtProvider.validateToken(token)) {
            throw new IllegalArgumentException("JWT Token không hợp lệ hoặc đã hết hạn");
        }

        Claims claims = jwtProvider.extractClaims(token);
        String email = claims.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin người dùng"));

        return mapToAuthResponse(user, token);
    }

    private AuthResponse mapToAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .departmentId(user.getDepartmentId())
                .departmentName(user.getDepartmentName())
                .doctorId(user.getDoctorId())
                .pharmacistId(user.getPharmacistId())
                .build();
    }
}