package com.capstone2024.scss.infrastructure.repositories.counselor;

import com.capstone2024.scss.domain.counselor.entities.AvailableDateRange;
import com.capstone2024.scss.domain.counselor.entities.Counselor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvailableDateRangeRepository extends JpaRepository<AvailableDateRange, Long> {

    Optional<AvailableDateRange> findByCounselor(Counselor counselor);

    boolean existsByCounselor(Counselor counselor);

    void deleteByCounselor(Counselor counselor);
}
