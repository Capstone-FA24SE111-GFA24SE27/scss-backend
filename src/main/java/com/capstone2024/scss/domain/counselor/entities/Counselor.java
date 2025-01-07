package com.capstone2024.scss.domain.counselor.entities;

import com.capstone2024.scss.domain.account.entities.Profile;
import com.capstone2024.scss.domain.contribution_question_card.entities.ContributionQuestionCard;
import com.capstone2024.scss.domain.counseling_booking.entities.CounselingSlot;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.AppointmentFeedback;
import com.capstone2024.scss.domain.counselor.entities.enums.CounselorStatus;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionCard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "counselor")
@PrimaryKeyJoinColumn(name = "profile_id")
public class Counselor extends Profile {

//    @Column(name = "rating")
//    private BigDecimal rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CounselorStatus status;

    @Column(name = "specialized_skills", columnDefinition = "TEXT")
    private String specializedSkills;

    @Column(name = "other_skills", columnDefinition = "TEXT")
    private String otherSkills;

    @Column(name = "work_history", columnDefinition = "TEXT")
    private String workHistory;

    @Column(name = "achievements", columnDefinition = "TEXT")
    private String achievements;

    @OneToMany(mappedBy = "counselor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Qualification> qualifications;

    @OneToMany(mappedBy = "counselor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Certification> certifications;

//    @ManyToMany
//    @JoinTable(
//            name = "counselor_slot",
//            joinColumns = @JoinColumn(name = "counselor_id"),
//            inverseJoinColumns = @JoinColumn(name = "slot_id")
//    )
//    private List<CounselingSlot> counselingSlots;

    @OneToMany(mappedBy = "counselor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SlotOfCounselor> slotOfCounselors;

    @OneToOne(mappedBy = "counselor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AvailableDateRange availableDateRange;

    @OneToMany(mappedBy = "counselor", fetch = FetchType.LAZY) // Link with QuestionCard
    private List<QuestionCard> questionCards;

    @OneToMany(mappedBy = "counselor", fetch = FetchType.LAZY) // Link with QuestionCard
    private List<ContributionQuestionCard> contributionQuestionCards;

    @OneToMany(mappedBy = "counselor")
    private List<AppointmentFeedback> feedbackList;
}
