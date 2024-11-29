package com.capstone2024.scss.domain.support_staff.entity;

import com.capstone2024.scss.domain.account.entities.Profile;
import com.capstone2024.scss.domain.demand.entities.StudentFollowing;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "support_staff")
@PrimaryKeyJoinColumn(name = "profile_id")
public class SupportStaff extends Profile {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SupportStaffStatus status;

    @OneToMany(mappedBy = "supportStaff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentFollowing> followings;

    public enum SupportStaffStatus {
        AVAILABLE, UNAVAILABLE
    }
}
