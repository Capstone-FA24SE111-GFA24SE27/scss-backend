package com.capstone2024.gym_management_system.infrastructure.repositories;

import com.capstone2024.gym_management_system.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounselingAppointmentRepository extends JpaRepository<CounselingAppointment, Long> {
}
