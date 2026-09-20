package Clinic_Management.DoctorScheduleService.repository;


import Clinic_Management.DoctorScheduleService.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByDepartmentIdAndActiveTrue(Long departmentId);
}