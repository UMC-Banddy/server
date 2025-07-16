package com.umc.banddy.domain.chat.service;

import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.ChatRoomParticipant;
import com.umc.banddy.domain.chat.domain.enums.RoomType;
import com.umc.banddy.domain.chat.repository.ChatMessageRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomParticipantRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomRepository;
import com.umc.banddy.domain.chat.web.dto.*;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.umc.banddy.domain.chat.converter.chatConveter.toChatMessageResponse;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    // 채팅 메세지 저장
    public ChatMessage saveMessage(
            Principal principal,
            ChatMessageRequest messageRequest ,
            Long roomId
    ){
        if (principal == null) {
            throw new IllegalStateException("Principal이 null 입니다. 인증이 필요한 요청입니다.");
        }
        MessageAuthenticationHeader auth = (MessageAuthenticationHeader) principal;

        ChatMessage chatMessage = ChatMessage.builder()
                .member(memberRepository.findById(auth.getMemberId())
                        .orElseThrow(() -> new IllegalArgumentException("잘못된 참여자")))
                .chatRoom(chatRoomRepository.findById(roomId)
                        .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다.")))
                .content(messageRequest.getContent())
                .build();

        return chatMessageRepository.save(chatMessage);
    }

    // 채팅 응답 반환
    public ChatMessageResponse chatToResponse(ChatMessage chatMessage) {return toChatMessageResponse(chatMessage);}

    // 채팅 참여자 principalName 조회
    public String findReceiverEmail(Long receiverId) { return memberRepository.findEmailById(receiverId);}


    // 그룹 채팅방 생성
    public ChatRoomResponse createGroupChatRoom(ChatRoomRequest requset){
        ChatRoom chatRoom = ChatRoom.builder()
                .name(requset.getRoomName())
                .imageUrl(requset.getImageUrl())
                .roomType(RoomType.GROUP)
                .build();

        // 채팅방 생성
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        List<ChatRoomResponse.RoomMemberinfo> memberinfos =new ArrayList<>();

        // 참여자 등록
        for(Long memberId : requset.getMemberIds()){
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다. ID: " + memberId));
            ChatRoomParticipant participant = ChatRoomParticipant.builder()
                    .chatRoom(savedRoom)
                    .member(member)
                    .lastReadAt(LocalDateTime.now()) // 초기값 설정
                    .build();
            participantRepository.save(participant);
            // 응답용 데이터
            memberinfos.add(
                    ChatRoomResponse.RoomMemberinfo.builder()
                    .userId(member.getId())
                    .userName(member.getNickname())
                    .build());
        }

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
            Principal principal,
            PrivateChatRoomRequest request
    ) {
        if (principal == null) {
            throw new IllegalStateException("Principal이 null 입니다. 인증이 필요한 요청입니다.");
        }

        Member member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Member friend = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구입니다."));

        ChatRoom chatRoom = ChatRoom.builder()
                .name(friend.getNickname() + "," + member.getNickname())
                .imageUrl(null)
                .roomType(RoomType.PRIVATE)
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // 참여자 추가
        saveParticipant(savedRoom.getId(), member.getId());
        saveParticipant(savedRoom.getId(), request.getMemberId());

        return PrivateChatRoomResponse.builder()
                .roomId(savedRoom.getId())
                .build();

    }

    public void saveParticipant(Long roomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버 없음"));

        ChatRoomParticipant participant = ChatRoomParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .lastReadAt(LocalDateTime.now())  // 초기값
                .build();

        participantRepository.save(participant);
    }

    public void markLastRead(Long roomId, String email){
        ChatRoomParticipant participant = participantRepository.findByChatRoomIdAndEmail(roomId, email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 참여자입니다."));

        participant.setLastReadAt(LocalDateTime.now());
        participantRepository.save(participant);
    }

    public Long extractRoomId(String dest) {
        if (dest == null) {
            return null;
        }
        // "/" 로 나눠서 마지막 요소를 가져옴
        String[] parts = dest.split("/");
        String last = parts[parts.length - 1];
        try {
            return Long.valueOf(last);
        } catch (NumberFormatException e) {
            // 경로 형식이 예상과 다를 경우 널 리턴 또는 예외 처리
            return null;
        }
    }

}
