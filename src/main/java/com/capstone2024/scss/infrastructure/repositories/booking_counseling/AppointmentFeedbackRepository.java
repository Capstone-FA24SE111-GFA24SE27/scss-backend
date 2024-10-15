package com.capstone2024.scss.infrastructure.repositories.booking_counseling;

import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.AppointmentFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentFeedbackRepository extends JpaRepository<AppointmentFeedback, Long> {
}