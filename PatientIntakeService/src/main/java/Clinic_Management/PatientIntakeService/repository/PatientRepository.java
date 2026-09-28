package Clinic_Management.PatientIntakeService.repository;

import Clinic_Management.PatientIntakeService.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByIdentityCardNumber(String identityCardNumber);
    Optional<Patient> findByInsuranceCode(String insuranceCode);
}