package Clinic_Management.DoctorScheduleService.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pharmacists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pharmacist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    private String licenseNumber; // e.g. "CCHN-00912/HCM"

    private String defaultCounter; // counter or station where the pharmacist is usually assigned

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Builder.Default
    private Boolean active = true;
}