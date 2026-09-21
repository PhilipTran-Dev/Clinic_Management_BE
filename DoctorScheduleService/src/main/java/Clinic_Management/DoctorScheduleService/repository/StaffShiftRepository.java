package Clinic_Management.DoctorScheduleService.repository;
import Clinic_Management.DoctorScheduleService.entity.Role;
import Clinic_Management.DoctorScheduleService.entity.StaffShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StaffShiftRepository extends JpaRepository<StaffShift, Long> {
    List<StaffShift> findByShiftDate(LocalDate shiftDate);
    List<StaffShift> findByShiftDateAndRole(LocalDate shiftDate, Role role);
}