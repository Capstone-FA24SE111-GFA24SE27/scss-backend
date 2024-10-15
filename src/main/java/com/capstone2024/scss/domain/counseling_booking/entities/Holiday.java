package com.capstone2024.scss.domain.counseling_booking.entities;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.counseling_booking.enums.HolidayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "holiday")
public class Holiday extends BaseEntity {

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "type", nullable = false)
//    private HolidayType type;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;
//    @AssertTrue(message = "Invalid date range for the specified holiday type.")
//    public boolean isValidDateRange() {
//        if (type == HolidayType.SINGLE_DAY) {
//            return startDate.equals(endDate);
//        } else if (type == HolidayType.MULTIPLE_DAYS) {
//            return !startDate.isAfter(endDate);
//        }
//        return false; // In case a new type is added without updating this method
//    }
}
