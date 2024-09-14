package com.capstone2024.scss.domain.counseling_booking.entities.counselor;

import com.capstone2024.scss.domain.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "expertise")
public class Expertise extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

//    @OneToMany(mappedBy = "expertise")
//    private Set<Counselor> counselors;
}
