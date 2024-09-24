package com.capstone2024.scss.domain.counselor.entities;

import com.capstone2024.scss.domain.account.entities.Profile;
import com.capstone2024.scss.domain.counseling_booking.entities.counseling_appointment.AppointmentFeedback;
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
@Table(name = "counselor")
@PrimaryKeyJoinColumn(name = "profile_id")
public class Counselor extends Profile {

    @Column(name = "rating")
    private BigDecimal rating;

    @OneToMany(mappedBy = "counselor")
    private List<AppointmentFeedback> feedbackList;

//    @ManyToOne
//    @JoinColumn(name = "expertise_id", nullable = false)
//    private Expertise expertise;
}
