package com.capstone2024.gym_management_system.domain.counseling_booking.entities.counselor;

import com.capstone2024.gym_management_system.domain.account.entities.Profile;
import com.capstone2024.gym_management_system.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

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

//    @ManyToOne
//    @JoinColumn(name = "expertise_id", nullable = false)
//    private Expertise expertise;
}
