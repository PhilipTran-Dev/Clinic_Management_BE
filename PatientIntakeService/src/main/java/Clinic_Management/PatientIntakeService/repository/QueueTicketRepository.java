package Clinic_Management.PatientIntakeService.repository;

import Clinic_Management.PatientIntakeService.entity.QueueStatus;
import Clinic_Management.PatientIntakeService.entity.QueueTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QueueTicketRepository extends JpaRepository<QueueTicket, Long> {

    @Query("SELECT COUNT(q) FROM QueueTicket q WHERE q.appointmentDate = :date AND q.departmentId = :departmentId")
    long countByAppointmentDateAndDepartmentId(LocalDate date, Long departmentId);

    @Query("""
        SELECT q FROM QueueTicket q
        WHERE q.doctorId = :doctorId
          AND q.appointmentDate = :date
          AND q.status IN :statuses
        ORDER BY
          CASE q.priorityLevel
            WHEN 'P1' THEN 1
            WHEN 'P2' THEN 2
            WHEN 'P3' THEN 3
          END ASC,
          q.checkInTime ASC
    """)
    List<QueueTicket> findDoctorActiveQueue(Long doctorId, LocalDate date, List<QueueStatus> statuses);
    List<QueueTicket> findByDepartmentIdAndAppointmentDateAndStatusOrderByCheckInTimeAsc(
            Long departmentId, LocalDate appointmentDate, QueueStatus status);
    Optional<QueueTicket> findByTicketNumber(String ticketNumber);
}