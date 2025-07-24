package com.umc.banddy.domain.chat.service;


import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.ChatRoomParticipant;
import com.umc.banddy.domain.chat.domain.enums.Role;
import com.umc.banddy.domain.chat.domain.enums.RoomType;
import com.umc.banddy.domain.chat.repository.ChatRoomParticipantRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomRepository;
import com.umc.banddy.domain.chat.web.dto.ChatRoom.ChatRoomRequest;
import com.umc.banddy.domain.chat.web.dto.ChatRoom.ChatRoomResponse;
import com.umc.banddy.domain.chat.web.dto.ChatRoom.PrivateChatRoomRequest;
import com.umc.banddy.domain.chat.web.dto.ChatRoom.PrivateChatRoomResponse;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.enums.Status;
import com.umc.banddy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final MemberRepository memberRepository;

    // 그룹 채팅방 생성
    public ChatRoomResponse createGroupChatRoom(Long memberId, ChatRoomRequest requset){
        ChatRoom chatRoom = ChatRoom.builder()
                .name(requset.getRoomName())
                .imageUrl(requset.getImageUrl())
                .roomType(RoomType.GROUP)
                .build();

        // 채팅방 생성
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // 참여자 등록
        List<ChatRoomParticipant> participantList = new ArrayList<>();
        List<ChatRoomResponse.RoomMemberinfo> memberinfos =new ArrayList<>();

        for(Long memberIds : requset.getMemberIds()){
            Member member = memberRepository.findById(memberIds)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다. ID: " + memberIds));

            ChatRoomParticipant participant = ChatRoomParticipant.builder()
                    .chatRoom(savedRoom)
                    .member(member)
                    .role(member.getId().equals(memberId)
                            ? Role.ADMIN
                            : Role.MEMBER) // 테스트 용
                    .status(Status.ACTIVE)
                    .lastReadAt(LocalDateTime.now()) // 초기값 설정
                    .build();
            participantList.add(participant);
            memberinfos.add(
                    ChatRoomResponse.RoomMemberinfo.builder()
                            .userId(member.getId())
                            .userName(member.getNickname())
                            .build());
        }

        participantRepository.saveAll(participantList);
        return ChatRoomResponse.builder()
                .roomId(savedRoom.getId())
                .roomName(savedRoom.getName())
                .roomImageUrl(savedRoom.getImageUrl())
                .lastMessageTime(LocalDateTime.now()) // 초기값 설정
                .pinnedAt(null)
                .roomtype(savedRoom.getRoomType())
                .memberinfos(memberinfos)
                .build();
    }

    // 개인 채팅방 생성
    public PrivateChatRoomResponse createPrivateChatRoom(
            Long memberId,
            PrivateChatRoomRequest request
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Member friend = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구입니다."));
        ChatRoom chatRoom = ChatRoom.builder()
                .name(null)
                .imageUrl(null)
                .roomType(RoomType.PRIVATE)
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // 참여자 추가
        saveParticipant(savedRoom, member);
        saveParticipant(savedRoom, friend);

        return PrivateChatRoomResponse.builder()
                .roomId(savedRoom.getId())
                .build();
    }

    public void saveParticipant(ChatRoom chatRoom, Member member) {
        ChatRoomParticipant participant = ChatRoomParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .role(Role.MEMBER)  // 기본 역할 설정
                .status(Status.ACTIVE)  // 기본 상태 설정
                .lastReadAt(LocalDateTime.now())  // 초기값
                .build();

        participantRepository.save(participant);
    }

}
