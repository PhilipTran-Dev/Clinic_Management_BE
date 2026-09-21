package Clinic_Management.DoctorScheduleService.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nurses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nurse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    private String qualification; // University degree or certification

    private String assignedStation; // Workstation or department where the nurse is assigned

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    private Boolean active = true;
}