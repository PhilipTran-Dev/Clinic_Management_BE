package Clinic_Management.DoctorScheduleService.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; //code of the department, e.g., "CARD" for Cardiology

    @Column(nullable = false)
    private String name; // name of the department, e.g., "Cardiology"
}