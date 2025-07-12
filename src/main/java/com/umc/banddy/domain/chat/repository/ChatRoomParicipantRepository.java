package com.umc.banddy.domain.chat.repository;

import com.umc.banddy.domain.chat.entity.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomParicipantRepository extends JpaRepository<ChatRoomParticipant, Long> {


}
