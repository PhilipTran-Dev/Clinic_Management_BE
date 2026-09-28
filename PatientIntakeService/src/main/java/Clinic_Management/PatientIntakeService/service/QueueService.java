package Clinic_Management.PatientIntakeService.service;

import Clinic_Management.PatientIntakeService.entity.QueueStatus;
import Clinic_Management.PatientIntakeService.entity.QueueTicket;
import Clinic_Management.PatientIntakeService.repository.QueueTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueTicketRepository queueTicketRepository;

    @Transactional(readOnly = true)
    public List<QueueTicket> getDoctorActiveQueue(Long doctorId, LocalDate date) {
        return queueTicketRepository.findDoctorActiveQueue(
                doctorId, date, List.of(QueueStatus.WAITING, QueueStatus.CALLED)
        );
    }

    @Transactional
    public QueueTicket callNextPatient(Long doctorId, LocalDate date) {
        List<QueueTicket> waitingList = queueTicketRepository.findDoctorActiveQueue(
                doctorId, date, List.of(QueueStatus.WAITING)
        );

        if (waitingList.isEmpty()) {
            throw new IllegalStateException("Hàng đợi không có bệnh nhân nào đang chờ.");
        }

        QueueTicket nextTicket = waitingList.get(0);
        nextTicket.setStatus(QueueStatus.CALLED);
        nextTicket.setCalledAt(LocalDateTime.now());
        return queueTicketRepository.save(nextTicket);
    }

    @Transactional
    public QueueTicket updateTicketStatus(String ticketNumber, QueueStatus status) {
        QueueTicket ticket = queueTicketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu: " + ticketNumber));

        ticket.setStatus(status);
        return queueTicketRepository.save(ticket);
    }
}