package com.umc.banddy.domain.chat.service;

import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.repository.ChatRoomParticipantRepository;
import com.umc.banddy.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRoomParticipantCache {

    private final ChatRoomParticipantRepository chatRoomParticipantRepository;

    @Cacheable(value = "roomParticipants", key = "#roomId")
    public Set<String> getParticipants(Long roomId) {
        List<String> emails = chatRoomParticipantRepository.findActiveEmailsByRoomId(roomId);
        return new HashSet<>(emails);
    }

//    @CacheEvict(value = "roomParticipants", key = "#roomId")
//    public void invalidateRoom(Long roomId) {
//        // 참여자 변경 시 캐시 무효화
//    }

    //    private final Map<Long, Set<String>> roomParticipantsMap = new ConcurrentHashMap<>();
//    // 참여자 조회: 캐시 miss 시 DB 조회 → 캐시 등록
//    public Set<String> getParticipants(Long roomId) {
//        return roomParticipantsCache.get(roomId, id -> {
//            List<String> emails = chatRoomParticipantRepository.findEmailsByRoomId(id);
//            return new HashSet<>(emails);
//        });
//    }
//
//    // 참여 여부 확인
//    public boolean isParticipant(Long roomId, String email) {
//        return getParticipants(roomId).contains(email);
//    }
//
//    // 방 참여자 변경 시 캐시 무효화
//    public void invalidateRoom(Long roomId) {
//        roomParticipantsCache.invalidate(roomId);
//    }
//
//    // 전체 캐시 초기화 (optional)
//    public void clearAll() {
//        roomParticipantsCache.invalidateAll();
//    }
}
