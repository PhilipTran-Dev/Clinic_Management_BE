package Clinic_Management.DoctorScheduleService.repository;

import Clinic_Management.DoctorScheduleService.entity.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NurseRepository extends JpaRepository<Nurse, Long> {
    List<Nurse> findByActiveTrue();
}