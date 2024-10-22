package com.capstone2024.scss.infrastructure.repositories._and_a;

import com.capstone2024.scss.domain.q_and_a.entities.Topic;
import com.capstone2024.scss.domain.q_and_a.enums.TopicType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    // Phương thức tìm các chủ đề theo loại
    @Query("SELECT t FROM Topic t WHERE t.type = :type")
    List<Topic> findByType(@Param("type") TopicType type);
}
