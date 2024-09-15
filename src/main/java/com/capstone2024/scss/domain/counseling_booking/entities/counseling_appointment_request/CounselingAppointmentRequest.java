package com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.CounselingAppointmentRequestStatus;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_request.enums.MeetingType;
import com.capstone2024.scss.domain.counseling_booking.entities.counselor.Counselor;
import com.capstone2024.scss.domain.counseling_booking.entities.student.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "counseling_appointment_request")
public class CounselingAppointmentRequest extends BaseEntity {

    @Column(name = "require_date")
    private LocalDate requireDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CounselingAppointmentRequestStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type")
    private MeetingType meetingType;

    @Column(name = "reason")
    private String reason;

    @ManyToOne
    @JoinColumn(name = "counselor_id")
    private Counselor counselor;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}