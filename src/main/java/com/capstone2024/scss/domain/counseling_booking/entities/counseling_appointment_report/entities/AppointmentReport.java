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

    // Fields from Intervention
    @Column(name = "intervention_type", nullable = false)
    private String interventionType;

    @Column(name = "intervention_description", nullable = false)
    private String interventionDescription;

    // Fields from ConsultationConclusion
    @Column(name = "counselor_conclusion", columnDefinition = "TEXT")
    private String counselorConclusion;

    @Column(name = "follow_up_needed", columnDefinition = "BIT")
    private boolean followUpNeeded;

    @Column(name = "follow_up_notes", columnDefinition = "TEXT")
    private String followUpNotes;

    // Fields from ConsultationContent
    @Column(name = "summary_of_discussion", columnDefinition = "TEXT")
    private String summaryOfDiscussion;

    @Column(name = "main_issues", columnDefinition = "TEXT")
    private String mainIssues;

    @Column(name = "student_emotions", columnDefinition = "TEXT")
    private String studentEmotions;

    @Column(name = "student_reactions", columnDefinition = "TEXT")
    private String studentReactions;

    // Fields from ConsultationGoal
    @Column(name = "specific_goal", columnDefinition = "TEXT")
    private String specificGoal;

    @Column(name = "goal_reason", columnDefinition = "TEXT")
    private String reason;
    //

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
