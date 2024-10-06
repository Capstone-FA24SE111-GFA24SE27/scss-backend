package com.capstone2024.scss.infrastructure.repositories;

import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_report.entities.AppointmentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentReportRepository extends JpaRepository<AppointmentReport, Long> { }
