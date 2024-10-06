package com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment_report.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.CounselingAppointment;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import com.capstone2024.scss.domain.student.entities.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment_report")
public class AppointmentReport extends BaseEntity {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "consultation_goal_id", referencedColumnName = "id")
    private ConsultationGoal consultationGoal;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "consultation_content_id", referencedColumnName = "id")
    private ConsultationContent consultationContent;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "consultation_conclusion_id", referencedColumnName = "id")
    private ConsultationConclusion consultationConclusion;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "intervention_id", nullable = false)
    private Intervention intervention;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "counselor_id", nullable = false)
    private Counselor counselor;

    @OneToOne
    @JoinColumn(name = "counseling_appointment_id", nullable = false)
    private CounselingAppointment counselingAppointment;
}
