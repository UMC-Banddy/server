package com.umc.banddy.domain.chat.service;


import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.ChatRoomParticipant;
import com.umc.banddy.domain.chat.domain.enums.Role;
import com.umc.banddy.domain.chat.domain.enums.RoomType;
import com.umc.banddy.domain.chat.repository.ChatMessageRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomParticipantRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomRepository;
import com.umc.banddy.domain.chat.web.dto.ChatRoom.*;
import com.umc.banddy.domain.friend.domain.Friend;
import com.umc.banddy.domain.friend.repository.FriendRepository;
import com.umc.banddy.domain.friend.service.FriendService;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.enums.Status;
import com.umc.banddy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final MemberRepository memberRepository;
    private final FriendService friendService;
    private final FriendRepository friendRepository;

    // 그룹 채팅방 생성
    public ChatRoomResponse createGroupChatRoom(Long memberId, ChatRoomRequest request){
        ChatRoom chatRoom = ChatRoom.builder()
                .name(request.getRoomName())
                .imageUrl(request.getImageUrl())
                .roomType(RoomType.GROUP)
                .build();

        // 채팅방 생성
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // 참여자 등록
        List<ChatRoomParticipant> participantList = new ArrayList<>();
        List<ChatRoomResponse.RoomMemberinfo> memberinfos =new ArrayList<>();

        for(Long memberIds : request.getMemberIds()){
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
                //.pinnedAt(null)
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
                .name("1대1채팅")
                .imageUrl("")
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

    public ChatRoomListResponse getMyChatRooms(Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 내 참여 정보 불러오기
        List<ChatRoomParticipant> participants
                = participantRepository.findAllGroupAndBandWithRoomParticipantsAndBandChatByMember(member);

        // 내가 속한 방 목록
        List<ChatRoom> myRooms = participants.stream()
                .map(ChatRoomParticipant::getChatRoom)
                .toList();

        // 채팅방 타입별로 분류
        Map<RoomType, List<ChatRoom>> roomsByType = myRooms.stream()
                .distinct()
                .collect(Collectors.groupingBy(ChatRoom::getRoomType));

        // 그룹 채팅방
        List<ChatRoom> groupRooms =
                roomsByType.getOrDefault(RoomType.GROUP, Collections.emptyList());

        // 밴드 채팅방
        List<ChatRoom> bandRooms =
                roomsByType.getOrDefault(RoomType.BAND, Collections.emptyList());


        // 않읽은 메세지 수
        Map<Long, Long> unreadCountMap = getUnreadCountMap(member);

        // 마지막으로 보낸 메세지 시간
        List<ChatMessageRepository.LastMessageProjection> lasts =
                chatMessageRepository.findLastMessageAtForGroupAndBandByMember(member);
        Map<Long, LocalDateTime> lastAtMap = lasts.stream()
                .collect(Collectors.toMap(
                        ChatMessageRepository.LastMessageProjection::getRoomId,
                        ChatMessageRepository.LastMessageProjection::getLastMessageAt
                ));

        // 응답 생성
        List<ChatRoomInfo> chatRoomInfos = groupRooms.stream()
                .map(room ->{
                    List<MemberInfo> memberInfos = room.getParticipants().stream()
                            .map(p -> MemberInfo.builder()
                                            .memberId(p.getMember().getId())
                                            .nickname(p.getMember().getNickname())
                                            .profileImageUrl(p.getMember().getProfileImageUrl())
                                            .build()
                                    )
                                    .toList();

                    return ChatRoomInfo.builder()
                            .chatName(room.getName())
                            .imageUrl(room.getImageUrl())
                            .memberInfos(memberInfos)
                            .unreadCount(unreadCountMap.get(room.getId()))
                            .lastMessageAt(lastAtMap.get(room.getId()))
                            .build();
                }).toList();

        Map<Boolean, List<ChatRoom>> partitioned = bandRooms.stream()
                .collect(Collectors.partitioningBy(
                        room -> room.getBandChat().getBand().getManager().equals(member)
                ));


        Map<Long, List<ChatRoom>> roomsByBand = partitioned.get(true).stream()
                .collect(Collectors.groupingBy(
                        room -> room.getBandChat().getBand().getId()
                ));

        List<ManagedRoomInfo> managedRoomInfos = roomsByBand.entrySet().stream()
                .map(entry -> {
                    Long bandId = entry.getKey();
                    List<ChatRoom> rooms = entry.getValue();
                    Band band = rooms.get(0).getBandChat().getBand(); // 모두 동일한 밴드

                    List<InterviewRoomInfo> roomInfos = rooms.stream()
                            .map(room -> {
                                ChatRoomParticipant other = room.getParticipants().stream()
                                        .filter(p -> !p.getMember().equals(member))
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalStateException("상대 참가자가 없습니다."));

                                return InterviewRoomInfo.builder()
                                        .memberId(other.getMember().getId())
                                        .profileImageUrl(other.getMember().getProfileImageUrl())
                                        .session(room.getBandChat().getBandSession().getSession().toString())      // 세션 정보
                                        .lastMessageAt(lastAtMap.get(room.getId()))
                                        .unreadCount(unreadCountMap.getOrDefault(room.getId(), 0L))
                                        .build();
                                    }
                            )
                            .toList();

                    return ManagedRoomInfo.builder()
                            .bandId(bandId)
                            .bandName(band.getName())
                            .profileImageUrl(band.getProfileImageUrl())
                            .bandStatus(band.getStatus())
                            .recruitingSession(
                                    band.getBandSessions().stream()
                                            .map(bandSession -> bandSession.getSession().toString())
                                            .toList()
                            )
                            .rooms(roomInfos)
                            .build();
                })
                .toList();

        List<AppliedRoomInfo> appliedRoomInfos = partitioned.get(false).stream()
                .map(room -> {
                    Band band = room.getBandChat().getBand();
                    return AppliedRoomInfo.builder()
                            .bandId(band.getId())
                            .roomId(room.getId())
                            .bandName(band.getName())
                            .profileImageUrl(band.getProfileImageUrl())
                            .unreadCount(unreadCountMap.getOrDefault(room.getId(), 0L))
                            .lastMessageAt(lastAtMap.get(room.getId()))
                            .build();
                })
                .toList();

        return ChatRoomListResponse.builder()
                .chatRoomInfos(chatRoomInfos)
                .managedRoomInfos(managedRoomInfos)
                .appliedRoomInfos(appliedRoomInfos)
                .build();

    }

    public Map<Long, Long> getUnreadCountMap(Member member) {
        List<ChatMessageRepository.UnreadCountProjection> list =
                chatMessageRepository.findUnreadCountsForGroupAndBandByMember(member);

        return list.stream()
                .collect(Collectors.toMap(
                        ChatMessageRepository.UnreadCountProjection::getRoomId,
                        ChatMessageRepository.UnreadCountProjection::getUnreadCount
                ));
    }

    public FriendsChatRoomResponse getFriendsChatRoom(Long memberId){
        List<Friend> friends = friendRepository.findAll().stream()
                .filter(friend -> friend.getMemberId().equals(memberId) || friend.getFriendshipId().equals(memberId))
                .toList();
        List<Long> friendIds = friends.stream()
                .map(f -> f.getMemberId().equals(memberId)
                        ? f.getFriendshipId()
                        : f.getMemberId())
                .toList();
        if (friendIds.isEmpty()) {
            return new FriendsChatRoomResponse(Collections.emptyList());
        }

        List<ChatRoomParticipant> participants = participantRepository.findFriendParticipants(memberId, friendIds);

        List<FriendChatRoom> friendChatRooms = participants.stream()
                .map(p ->
                    FriendChatRoom.builder()
                            .roomId(p.getChatRoom().getId())
                            .memberId(p.getMember().getId())
                            .friendName(p.getMember().getNickname())
                            .profileImage(p.getMember().getProfileImageUrl())
                            .build()
                ).toList();
        return FriendsChatRoomResponse.builder()
                .rooms(friendChatRooms)
                .build();
    }

//    public ParticipantInfos getChatRoomInfo(ChatRoom chatRoom, Member member){
//
//
//
//
//
//
//        return ParticipantInfos.builder()
//                .build();
//    }

}
