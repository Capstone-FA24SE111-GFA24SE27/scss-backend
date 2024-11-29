package com.capstone2024.scss.domain.student.entities;

import com.capstone2024.scss.domain.account.entities.Profile;
import com.capstone2024.scss.domain.counselor.entities.Specialization;
import com.capstone2024.scss.domain.demand.entities.StudentFollowing;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionBan;
import com.capstone2024.scss.domain.q_and_a.entities.QuestionFlag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student")
@PrimaryKeyJoinColumn(name = "profile_id")
public class Student extends Profile {

    @Column(name = "student_code", nullable = false, unique = true)
    private String studentCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "major_id")
    private Major major;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionFlag> flags = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionBan> bans = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "counseling_profile_id", referencedColumnName = "id")
    private StudentCounselingProfile counselingProfile;

//    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<StudentFollowing> followings;

//    public boolean checkQuestionBan() {
//        // Lấy lệnh "ban" gần đây nhất (nếu có)
//        Optional<QuestionBan> lastBan = this.bans.stream()
//                .max(Comparator.comparing(QuestionBan::getBanEndDate)); // Lấy ban có banEndDate lớn nhất
//
//        // Kiểm tra nếu tồn tại ban và ngày hiện tại nhỏ hơn banEndDate
//        if (lastBan.isPresent()) {
//            LocalDateTime currentDate = LocalDateTime.now();
//            return currentDate.isBefore(lastBan.get().getBanEndDate());
//        }
//
//        return false;
//    }
}
