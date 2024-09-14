package com.capstone2024.scss.infrastructure.repositories;

import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounselingAppointmentRepository extends JpaRepository<CounselingAppointment, Long> {
}
