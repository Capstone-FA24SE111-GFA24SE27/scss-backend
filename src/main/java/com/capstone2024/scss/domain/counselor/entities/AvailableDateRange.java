package com.capstone2024.scss.domain.counselor.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "available_date_range")
public class AvailableDateRange extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "counselor_id", nullable = false, unique = true)
    private Counselor counselor;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @AssertTrue(message = "Start date must be before end date")
    public boolean isValidDateRange() {
        return startDate.isBefore(endDate);
    }
}
