package com.umc.banddy.domain.chat.repository;

import com.umc.banddy.domain.chat.entity.ChatMessage;
import com.umc.banddy.domain.chat.entity.UserTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
}
