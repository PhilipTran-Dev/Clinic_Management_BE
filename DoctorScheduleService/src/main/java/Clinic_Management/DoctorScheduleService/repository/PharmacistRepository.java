package Clinic_Management.DoctorScheduleService.repository;

import Clinic_Management.DoctorScheduleService.entity.Pharmacist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacistRepository extends JpaRepository<Pharmacist, Long> {
    List<Pharmacist> findByActiveTrue();
}