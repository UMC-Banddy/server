package com.umc.banddy.domain.chat.service;


import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.BandChat;
import com.umc.banddy.domain.band.profile.domain.mapping.BandSession;
import com.umc.banddy.domain.band.profile.repository.BandChatRepository;
import com.umc.banddy.domain.band.profile.repository.BandRepository;
import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.ChatRoomParticipant;
import com.umc.banddy.domain.chat.domain.enums.PassFail;
import com.umc.banddy.domain.chat.domain.enums.Role;
import com.umc.banddy.domain.chat.domain.enums.RoomType;
import com.umc.banddy.domain.chat.repository.ChatMessageRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomParticipantRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomRepository;
import com.umc.banddy.domain.chat.web.dto.chatroom.*;
import com.umc.banddy.domain.chat.web.dto.chatroom.creation.*;
import com.umc.banddy.domain.chat.web.dto.chatroom.roomlist.*;
import com.umc.banddy.domain.friend.domain.Friend;
import com.umc.banddy.domain.friend.repository.FriendRepository;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.enums.Status;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.global.infra.S3Uploader;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final BandRepository bandRepository;
    private final BandChatRepository bandChatRepository;
    private final S3Uploader s3Uploader;


    // 그룹 채팅방 생성
    public ChatRoomResponse createGroupChatRoom(Long memberId,  ChatRoomRequest request){

//        MultipartFile image = request.getImage();
//        String profileImageUrl = (image != null && !image.isEmpty())
//                ? s3Uploader.upload(image, "group-chat-images") : null;
        ChatRoom chatRoom = ChatRoom.builder()
                .name(request.getRoomName())
                .imageUrl(null)
                .roomType(RoomType.GROUP)
                .build();

        // 채팅방 생성
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // 참여자 등록
        List<Long> memberIds = request.getMemberIds();
        memberIds.add(memberId);

        List<Member> members = memberRepository.findAllById(memberIds); // 요청한 멤버 ID 포함

        List<ChatRoomParticipant> participantList  = members.stream()// 요청한 멤버는 제외
                .map(member -> ChatRoomParticipant.builder()
                        .chatRoom(savedRoom)
                        .member(member)
                        .role(Role.MEMBER) // 테스트 용
                        .status(Status.ACTIVE)
                        .lastReadAt(LocalDateTime.now()) // 초기값 설정
                        .build())
                .toList();
        List<ChatRoomResponse.RoomMemberinfo> memberinfos = members.stream()
                .filter(member -> !member.getId().equals(memberId))
                .map( member -> {
                    return ChatRoomResponse.RoomMemberinfo.builder()
                            .memberId(member.getId())
                            .memberName(member.getNickname())
                            .build();
                }).toList();

        participantRepository.saveAll(participantList);
        return ChatRoomResponse.builder()
                .roomId(savedRoom.getId())
                .roomName(savedRoom.getName())
                //.roomImageUrl(savedRoom.getImageUrl())
                .lastMessageTime(LocalDateTime.now()) // 초기값 설정
                //.pinnedAt(null)
                .roomtype(savedRoom.getRoomType())
                .memberinfos(memberinfos)
                .build();
    }


    public UpdateGroupChatResponse updateGroupChatRoom(
            Long memberId,
            UpdateGroupChatRequest request
    ) {

        ChatRoom chatRoom = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));


        if(chatRoom.getRoomType()!=RoomType.GROUP){
            throw new IllegalArgumentException("그룹 채팅방이 아닙니다.");
        }
//        MultipartFile image = request.getImage();
//
//        // 채팅방 정보 업데이트
//        chatRoom.setName(request.getRoomName());
//        if(image != null && !image.isEmpty()) {
//            chatRoom.setImageUrl(s3Uploader.upload(image, "group-chat-images"));
//        }
        chatRoom.setName(request.getRoomName());
        chatRoomRepository.save(chatRoom);

        List<ChatRoomResponse.RoomMemberinfo> memberinfos = chatRoom.getParticipants().stream()
                .map(p -> ChatRoomResponse.RoomMemberinfo.builder()
                        .memberId(p.getMember().getId())
                        .memberName(p.getMember().getNickname())
                        .build())
                .toList();

        return UpdateGroupChatResponse.builder()
                .roomId(chatRoom.getId())
                .roomName(chatRoom.getName())
                .roomProfileUrl(chatRoom.getImageUrl())
                .build();
    }



    // 1:1 채팅 방 조회
    public PrivateChatRoomResponse getPrivateChatRoom(Long memberId, Long friendId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Member friend = memberRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구입니다."));

        // 조회 후 없으면 생성
        ChatRoom chatRoom = chatRoomRepository.findPrivateChatRoomByParticipants(member.getId(), friend.getId())
                .orElseGet(() -> createPrivateChatRoom(member, friend));

        return PrivateChatRoomResponse.builder()
                .roomId(chatRoom.getId())
                .build();
    }
    // 개인 채팅방 생성
    public ChatRoom createPrivateChatRoom(
            Member member,
            Member friend
    ) {
        ChatRoom chatRoom = ChatRoom.builder()
                .name(null)
                .imageUrl(null)
                .roomType(RoomType.PRIVATE)
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // 참여자 추가
        saveParticipant(savedRoom, member);
        saveParticipant(savedRoom, friend);

        return savedRoom;
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
                = participantRepository.findAllWithRoomParticipantsAndBandChatByMember(member);

        // 내가 속한 방 목록
        List<ChatRoom> myRooms = participants.stream()
                .map(ChatRoomParticipant::getChatRoom)
                .toList();

        // 채팅방 타입별로 분류
        Map<RoomType, List<ChatRoom>> roomsByType = myRooms.stream()
                .distinct()
                .collect(Collectors.groupingBy(ChatRoom::getRoomType));

        // 그룹 채팅방
        List<ChatRoom> groupAndPrivateRooms = Stream.of(
                        roomsByType.getOrDefault(RoomType.GROUP, Collections.emptyList()),
                        roomsByType.getOrDefault(RoomType.PRIVATE, Collections.emptyList())
                )
                .flatMap(List::stream)
                .toList();
        // 그룹 채팅방
        List<ChatRoom> groupRooms =
                roomsByType.getOrDefault(RoomType.GROUP, Collections.emptyList());
        // 개인 채팅방
        List<ChatRoom> privateRooms =
                roomsByType.getOrDefault(RoomType.PRIVATE, Collections.emptyList());

        // 밴드 채팅방
        List<ChatRoom> bandRooms =
                roomsByType.getOrDefault(RoomType.BAND, Collections.emptyList());

        // 안읽은 메세지 수
        Map<Long, Long> unreadCountMap = getUnreadCountMap(member);

        // 마지막으로 보낸 메세지 시간
        List<ChatMessageRepository.LastMessageProjection> lasts =
                chatMessageRepository.findLastMessageAtByMember(member);
        Map<Long, LocalDateTime> lastAtMap = lasts.stream()
                .collect(Collectors.toMap(
                        ChatMessageRepository.LastMessageProjection::getRoomId,
                        ChatMessageRepository.LastMessageProjection::getLastMessageAt
                ));

        // 응답 생성
        List<ChatRoomInfoDto> groupChatRoomInfos = groupRooms.stream()
                .map(room ->{
                    List<MemberInfo> memberInfos = room.getParticipants().stream()
                            .map(p -> MemberInfo.builder()
                                            .memberId(p.getMember().getId())
                                            .nickname(p.getMember().getNickname())
                                            .profileImageUrl(p.getMember().getProfileImageUrl())
                                            .build()
                                    )
                                    .toList();

                    return ChatRoomInfoDto.builder()
                            .roomType(String.valueOf(RoomType.GROUP))
                            .roomId(room.getId())
                            .chatName(room.getName())
                            .imageUrl(room.getImageUrl())
                            .memberInfos(memberInfos)
                            .unreadCount(unreadCountMap.get(room.getId()))
                            .lastMessageAt(lastAtMap.get(room.getId()))
                            .build();
                }).collect(Collectors.toList());

        List<PrivateChatRoomInfoDto> privateChatRoomInfos = privateRooms.stream()
                .map(room ->{

                    MemberInfo memberInfo = room.getParticipants().stream()
                            .filter(p -> p.getMember().getId().equals(memberId))
                            .map(p -> MemberInfo.builder()
                                    .memberId(p.getMember().getId())
                                    .nickname(p.getMember().getNickname())
                                    .profileImageUrl(p.getMember().getProfileImageUrl())
                                    .build())
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("내 정보가 없습니다."));


                    return PrivateChatRoomInfoDto.builder()
                            .roomType(String.valueOf(RoomType.PRIVATE))
                            .roomId(room.getId())
                            .chatName(room.getName())
                            .imageUrl(room.getImageUrl())
                            .memberInfo(memberInfo)
                            .unreadCount(unreadCountMap.get(room.getId()))
                            .lastMessageAt(lastAtMap.get(room.getId()))
                            .build();
                }).collect(Collectors.toList());

        Map<Boolean, List<ChatRoom>> partitioned = bandRooms.stream()
                .collect(Collectors.partitioningBy(
                        room -> room.getParticipants().stream()
                                .anyMatch(p -> p.getRole().equals(Role.BANDMANAGER))
                ));

        List<ChatRoom> adminBandRooms = partitioned.get(true);
        List<ChatRoom> nonAdminBandRooms = partitioned.get(false);

        List<ChatRoomInfoDto> nonAdminBandRoomInfos = nonAdminBandRooms.stream()
                .map(room ->{
                    List<MemberInfo> memberInfos = room.getParticipants().stream()
                            .map(p -> MemberInfo.builder()
                                    .memberId(p.getMember().getId())
                                    .nickname(p.getMember().getNickname())
                                    .profileImageUrl(p.getMember().getProfileImageUrl())
                                    .build()
                            )
                            .toList();

                    return ChatRoomInfoDto.builder()
                            .roomType("BAND-APPLICANT")
                            .roomId(room.getId())
                            .chatName(room.getBandChat().getBand().getName())
                            .imageUrl(room.getBandChat().getBand().getProfileImageUrl())
                            .memberInfos(memberInfos)
                            .unreadCount(unreadCountMap.get(room.getId()))
                            .lastMessageAt(lastAtMap.get(room.getId()))
                            .build();
                }).collect(Collectors.toList());

        List<BandManagerRoomInfoDto> adminBandRoomInfos = adminBandRooms.stream()
                .collect(Collectors.groupingBy(room -> room.getBandChat().getBand().getId()))
                .entrySet().stream()
                .map(entry -> {
                    Long bandId = entry.getKey();
                    List<ChatRoom> rooms = entry.getValue();

                    ChatRoom anyRoom = rooms.get(0);
                    Band band = anyRoom.getBandChat().getBand();

                    // 각 방에 대한 ChatRoomInfo 생성
                    List<BandManagerRoomInfoDto.BandChatRoomInfo> chatRoomInfos1 = rooms.stream()
                            .map(room -> {
                                MemberInfo memberInfo = room.getParticipants().stream()
                                        .map(ChatRoomParticipant::getMember)
                                       // .filter(member1 -> !member1.getId().equals(memberId))
                                        .map(member1 -> MemberInfo.builder()
                                                .memberId(member1.getId())
                                                .nickname(member1.getNickname())
                                                .profileImageUrl(member1.getProfileImageUrl())
                                                .build())
                                        .findFirst()
                                        .orElse(null);

                                return BandManagerRoomInfoDto.BandChatRoomInfo.builder()
                                        .roomId(room.getId())
                                        .memberInfo(memberInfo)
                                        .session(room.getBandChat().getBandSession().getSession().toString())
                                        .passFail(room.getBandChat().getPassFail())
                                        .lastMessageAt(lastAtMap.get(room.getId()))
                                        .UnreadCount(unreadCountMap.get(room.getId()))
                                        .build();
                            })
                            .toList();

                    // 1. 전체 UnreadCount 합산
                    long totalUnread = chatRoomInfos1.stream()
                            .mapToLong(info -> Optional.ofNullable(info.getUnreadCount()).orElse(0L))
                            .sum();

                    LocalDateTime latest = chatRoomInfos1.stream()
                            .map(BandManagerRoomInfoDto.BandChatRoomInfo::getLastMessageAt)
                            .filter(Objects::nonNull)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);

                    // 밴드 단위 DTO 조립
                    return BandManagerRoomInfoDto.builder()
                            .roomType("BAND-MANAGER")
                            .bandId(band.getId())
                            .bandName(band.getName())
                            .bandImageUrl(band.getProfileImageUrl())
                            .status(band.getStatus())
                            .bandSessionList(
                                    band.getBandSessions().stream()
                                            .filter(session -> "RECRUITING".equals(session.getSessionStatus()))
                                            .map(session -> session.getBand().getName())
                                            .distinct()
                                            .toList()
                            )
                            .chatRoomInfo(chatRoomInfos1)
                            .lastMessageAt(latest)
                            .unreadCount(totalUnread)
                            .build();
                })
                .collect(Collectors.toList());


        List<ChatRoomInfo> combined = Stream.concat(Stream.concat(
                        Stream.concat(
                                privateChatRoomInfos.stream(),
                                nonAdminBandRoomInfos.stream()
                        ),
                        adminBandRoomInfos.stream()
                ),
                        groupChatRoomInfos.stream()
                )
                .sorted(Comparator.comparing(
                        ChatRoomInfo::getLastMessageAt,
                        Comparator.nullsFirst(Comparator.reverseOrder())
                ))
                .toList();

//
//
//
//                .map(room ->{
//                    List<MemberInfo> memberInfos = room.getParticipants().stream()
//                            .map(p -> MemberInfo.builder()
//                                    .memberId(p.getMember().getId())
//                                    .nickname(p.getMember().getNickname())
//                                    .profileImageUrl(p.getMember().getProfileImageUrl())
//                                    .build()
//                            )
//                            .toList();
//
//                    return BandManagerRoomInfoDto.builder()
//                            .roomId(room.getId())
//                            .chatName(room.getBandChat().getBand().getName())
//                            .imageUrl(room.getBandChat().getBand().getProfileImageUrl())
//                            .memberInfos(memberInfos)
//                            .unreadCount(unreadCountMap.get(room.getId()))
//                            .lastMessageAt(lastAtMap.get(room.getId()))
//                            .build();
//                }).collect(Collectors.toList());
//
//
//
//        Map<Boolean, List<ChatRoom>> partitioned = bandRooms.stream()
//                .collect(Collectors.partitioningBy(
//                        room -> room.getBandChat().getBand().getManager().equals(member)
//                ));
//
//
//        Map<Long, List<ChatRoom>> roomsByBand = partitioned.get(true).stream()
//                .collect(Collectors.groupingBy(
//                        room -> room.getBandChat().getBand().getId()
//                ));
//
//        List<ManagedRoomInfo> managedRoomInfos = roomsByBand.entrySet().stream()
//                .map(entry -> {
//                    Long bandId = entry.getKey();
//                    List<ChatRoom> rooms = entry.getValue();
//                    Band band = rooms.get(0).getBandChat().getBand(); // 모두 동일한 밴드
//
//                    List<InterviewRoomInfo> roomInfos = rooms.stream()
//                            .map(room -> {
//                                ChatRoomParticipant other = room.getParticipants().stream()
//                                        .filter(p -> !p.getMember().equals(member))
//                                        .findFirst()
//                                        .orElseThrow(() -> new IllegalStateException("상대 참가자가 없습니다."));
//
//                                return InterviewRoomInfo.builder()
//                                        .memberId(other.getMember().getId())
//                                        .profileImageUrl(other.getMember().getProfileImageUrl())
//                                        .session(room.getBandChat().getBandSession().getSession().toString())      // 세션 정보
//                                        .lastMessageAt(lastAtMap.get(room.getId()))
//                                        .unreadCount(unreadCountMap.getOrDefault(room.getId(), 0L))
//                                        .build();
//                                    }
//                            )
//                            .toList();
//
//                    return ManagedRoomInfo.builder()
//                            .bandId(bandId)
//                            .bandName(band.getName())
//                            .profileImageUrl(band.getProfileImageUrl())
//                            .bandStatus(band.getStatus())
//                            .recruitingSession(
//                                    band.getBandSessions().stream()
//                                            .map(bandSession -> bandSession.getSession().toString())
//                                            .toList()
//                            )
//                            .rooms(roomInfos)
//                            .build();
//                })
//                .toList();
//
//        List<AppliedRoomInfo> appliedRoomInfos = partitioned.get(false).stream()
//                .map(room -> {
//                    Band band = room.getBandChat().getBand();
//                    return AppliedRoomInfo.builder()
//                            .bandId(band.getId())
//                            .roomId(room.getId())
//                            .bandName(band.getName())
//                            .profileImageUrl(band.getProfileImageUrl())
//                            .unreadCount(unreadCountMap.getOrDefault(room.getId(), 0L))
//                            .lastMessageAt(lastAtMap.get(room.getId()))
//                            .build();
//                })
//                .toList();

        return ChatRoomListResponse.builder()
                .chatRoomInfos(combined)
                .build();

    }

    public Map<Long, Long> getUnreadCountMap(Member member) {
        List<ChatMessageRepository.UnreadCountProjection> list =
                chatMessageRepository.findUnreadCountsByMember(member);

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

    @Transactional
    public ParticipantInfos getChatRoomInfo(ChatRoom chatRoom, Member member){

        List<ChatRoomParticipant> participants
                = participantRepository.findAllByChatRoom(chatRoom);

        List<ParticipantInfos.Info> infoList = new ArrayList<>();
        for(ChatRoomParticipant participant : participants){
            if(participant.getMember().getId().equals(member.getId())){
                participant.setLastReadAt(LocalDateTime.now());
            }
            ParticipantInfos.Info info = ParticipantInfos.Info.builder()
                    .memberId(participant.getMember().getId())
                    .timestamp(participant.getLastReadAt())
                    .build();
            infoList.add(info);
        }

        return ParticipantInfos.builder()
                .roomId(chatRoom.getId())
                .infos(infoList)
                .build();
    }


    @Transactional
    public BandJoinResponse joinBand(Long bandId, Long memberId, String session){

        Band band = bandRepository.findWithSessionsAndManager(bandId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 밴드입니다. ID: " + bandId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + memberId));

        RoomType roomType = RoomType.BAND;

        System.out.println(roomType );

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(band.getName() + " 밴드 채팅방");
        chatRoom.setImageUrl(band.getProfileImageUrl());
        chatRoom.setRoomType(roomType);

        chatRoomRepository.save(chatRoom);

        BandSession bandSession= band.getBandSessions().stream()
                .filter(bs -> bs.getSessionStatus().equals("RECRUITING"))
                .filter(bs -> bs.getSession().getName().equals(session))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상태의 밴드 세션입니다: " + session));

       bandChatRepository.save(
                BandChat.builder()
                        .band(band)
                        .passFail(PassFail.PENDING)
                        .bandSession(bandSession)
                        .chatRoom(chatRoom)
                        .build()
       );

        // 참여자 추가
        saveParticipant(chatRoom, member);
        ChatRoomParticipant bandManager = ChatRoomParticipant.builder()
                .chatRoom(chatRoom)
                .member(band.getManager())
                .role(Role.BANDMANAGER)  // 기본 역할 설정
                .status(Status.ACTIVE)  // 기본 상태 설정
                .lastReadAt(LocalDateTime.now())  // 초기값
                .build();
        participantRepository.save(bandManager);
        return BandJoinResponse.builder()
                .roomId(chatRoom.getId())
                .bandName(band.getName())
                .bandProfileUrl(band.getProfileImageUrl())
                .managerId(bandManager.getId())
                .managerName(band.getManager().getNickname())
                .managerProfileUrl(band.getManager().getProfileImageUrl())
                .build();
    }

}
