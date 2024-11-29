package com.capstone2024.scss.infrastructure.cron_job;

import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.enums.CounselingAppointmentStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.CounselingAppointmentRequest;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.CounselingAppointmentRequestStatus;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingAppointmentRepository;
import com.capstone2024.scss.infrastructure.repositories.booking_counseling.CounselingAppointmentRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AppointmentStatusScheduler {

    private final CounselingAppointmentRepository appointmentRepository;
    private final CounselingAppointmentRequestRepository requestRepository;

    @Scheduled(cron = "0 0 1 * * ?") // Chạy lúc 1 giờ sáng mỗi ngày
    @Transactional
    public void markWaitingAppointmentsAsAbsent() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN); // Đầu ngày hôm nay
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);   // Cuối ngày hôm nay

        // Lấy danh sách các appointment đang WAITING
        List<CounselingAppointment> waitingAppointments = appointmentRepository.findWaitingAppointmentsForToday(startOfDay, endOfDay);

        // Chuyển trạng thái thành ABSENT
        for (CounselingAppointment appointment : waitingAppointments) {
            appointment.setStatus(CounselingAppointmentStatus.ABSENT);
        }

        // Lưu thay đổi
        appointmentRepository.saveAll(waitingAppointments);

        System.out.println("Updated " + waitingAppointments.size() + " appointments to ABSENT.");
    }

    @Scheduled(cron = "0 0 2 * * ?") // Chạy lúc 2 giờ sáng mỗi ngày
    @Transactional
    public void expireWaitingRequests() {
        LocalDate yesterday = LocalDate.now().minusDays(1); // Lấy ngày hôm qua

        // Lấy danh sách các appointment request trạng thái WAITING của ngày hôm qua
        List<CounselingAppointmentRequest> waitingRequests = requestRepository.findWaitingRequestsForYesterday(yesterday);

        // Chuyển trạng thái thành EXPIRED
        for (CounselingAppointmentRequest request : waitingRequests) {
            request.setStatus(CounselingAppointmentRequestStatus.EXPIRED);
        }

        // Lưu thay đổi
        requestRepository.saveAll(waitingRequests);

        System.out.println("Updated " + waitingRequests.size() + " requests to EXPIRED.");
    }
}
