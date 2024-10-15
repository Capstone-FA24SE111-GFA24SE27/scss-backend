package com.capstone2024.scss.infrastructure.repositories._and_a;

import com.capstone2024.scss.domain.account.entities.Account;
import com.capstone2024.scss.domain.q_and_a.entities.Message;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.isRead = :forceRead WHERE m.chatSession.id = :chatSessionId AND m.sender = :sender")
    int readAllMessages(@Param("chatSessionId") Long chatSessionId, @Param("sender") Account sender, @Param("forceRead") boolean forceRead);
}
