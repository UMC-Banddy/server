package com.umc.banddy.domain.chat.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatRoomParticipantCache {

    // Key: roomId, Value: 참여자 이메일 Set (구독 여부와 무관)
    private final Map<Long, Set<String>> roomParticipantsMap = new ConcurrentHashMap<>();

    // 참여자 등록
    public void addParticipant(Long roomId, String email) {
        roomParticipantsMap
                .computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet())
                .add(email);
    }

    // 캐시 참여자 제거
    public void removeParticipant(Long roomId, String email) {
        Set<String> participants = roomParticipantsMap.get(roomId);
        if (participants != null) {
            participants.remove(email);
            if (participants.isEmpty()) {
                roomParticipantsMap.remove(roomId);
            }
        }
    }

    // 캐시 참여자 조회
    public boolean isParticipant(Long roomId, String email) {
        Set<String> participants = roomParticipantsMap.get(roomId);
        return participants != null && participants.contains(email);
    }

    // 전체 방에서 유저 제거
    public void removeUserFromAllRooms(String email) {
        for (Map.Entry<Long, Set<String>> entry : roomParticipantsMap.entrySet()) {
            entry.getValue().remove(email);
        }
        // 정리
        roomParticipantsMap.entrySet().removeIf(e -> e.getValue().isEmpty());
    }

    // 서버 리셋 등 전체 초기화
    public void clear() {
        roomParticipantsMap.clear();
    }
}
