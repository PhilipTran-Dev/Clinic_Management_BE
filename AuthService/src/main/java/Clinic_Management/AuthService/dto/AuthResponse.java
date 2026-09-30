package Clinic_Management.AuthService.dto;
import Clinic_Management.AuthService.entity.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String fullName;
    private UserRole role;
    private Long departmentId;
    private String departmentName;
    private Long doctorId;
    private Long pharmacistId;
}