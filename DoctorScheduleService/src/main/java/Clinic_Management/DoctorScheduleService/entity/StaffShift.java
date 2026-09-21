package Clinic_Management.DoctorScheduleService.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "staff_shifts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "shift_date", "session"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String staffName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // NURSE or PHARMACIST

    @Column(nullable = false)
    private String assignedLocation; // Workstation or department where the staff member is assigned

    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShiftSession session; // MORNING or AFTERNOON
}