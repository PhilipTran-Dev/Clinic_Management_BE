package Clinic_Management.AuthService.dto;

import Clinic_Management.AuthService.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateStaffRequest {
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    private String phone;

    @NotNull(message = "Vai trò không được để trống")
    private UserRole role;

    private Long departmentId;
    private String departmentName;
    private Long doctorId;
    private Long pharmacistId;
}