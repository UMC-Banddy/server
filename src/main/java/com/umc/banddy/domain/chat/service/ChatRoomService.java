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
import com.umc.banddy.domain.mypage.notification.domain.Notification;
import com.umc.banddy.domain.mypage.notification.domain.mapping.ChatNotification;
import com.umc.banddy.domain.mypage.notification.enums.NotificationType;
import com.umc.banddy.domain.mypage.notification.enums.ReadStatus;
import com.umc.banddy.domain.mypage.notification.repository.ChatNotificationRepository;
import com.umc.banddy.domain.mypage.notification.repository.NotificationRepository;
import com.umc.banddy.global.infra.S3Uploader;
import lombok.RequiredArgsConstructor;

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
    private final ChatRoomParticipantCache chatRoomParticipantCache;
    private final ChatMessageService chatMessageService;
    private final ChatNotificationRepository chatNotificationRepository;
    private final NotificationRepository notificationRepository;


    // 그룹 채팅방 생성
    public ChatRoomResponse createGroupChatRoom(Long memberId, MultipartFile image,  ChatRoomRequest request){

        String profileImageUrl = (image != null && !image.isEmpty())
                ? s3Uploader.upload(image, "group-chat-images") : null;
        ChatRoom chatRoom = ChatRoom.builder()
                .name(request.getRoomName())
                .imageUrl(profileImageUrl)
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
                        .lastReadMessageId(0L) // 초기값 설정
                        .build())
                .toList();
        List<ChatRoomResponse.RoomMemberInfo> memberinfos = members.stream()
                .filter(member -> !member.getId().equals(memberId))
                .map( member -> {
                    return ChatRoomResponse.RoomMemberInfo.builder()
                            .memberId(member.getId())
                            .memberName(member.getNickname())
                            .build();
                }).toList();

        participantRepository.saveAll(participantList);
        return ChatRoomResponse.builder()
                .roomId(savedRoom.getId())
                .roomName(savedRoom.getName())
                .roomImageUrl(savedRoom.getImageUrl())
                .lastMessageTime(LocalDateTime.now()) // 초기값 설정
                .roomtype(savedRoom.getRoomType())
                .memberInfos(memberinfos)
                .build();
    }

    // 그룹 채팅 방 업데이트
    public UpdateGroupChatResponse updateGroupChatRoom(
            Long memberId,
            MultipartFile image,
            UpdateGroupChatRequest request
    ) {

        ChatRoom chatRoom = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));


        if(chatRoom.getRoomType()!=RoomType.GROUP){
            throw new IllegalArgumentException("그룹 채팅방이 아닙니다.");
        }
//        MultipartFile image = request.getImage();
//
        // 채팅방 정보 업데이트
        if(request.getRoomName() != null && !request.getRoomName().isEmpty()){
            chatRoom.setName(request.getRoomName());
        }
        chatRoom.setName(request.getRoomName());
        if(image != null && !image.isEmpty()) {
            chatRoom.setImageUrl(s3Uploader.upload(image, "group-chat-images"));
        }
        chatRoom.setName(request.getRoomName());
        chatRoomRepository.save(chatRoom);

//        List<ChatRoomResponse.RoomMemberinfo> memberinfos = chatRoom.getParticipants().stream()
//                .map(p -> ChatRoomResponse.RoomMemberinfo.builder()
//                        .memberId(p.getMember().getId())
//                        .memberName(p.getMember().getNickname())
//                        .build())
//                .toList();

        return UpdateGroupChatResponse.builder()
                .roomId(chatRoom.getId())
                .roomName(chatRoom.getName())
                .roomProfileUrl(chatRoom.getImageUrl())
                .build();
    }



    // 1:1 채팅 방 조회
    @Transactional
    public BasicChatRoomInfo getPrivateChatRoom(Long memberId, Long friendId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Member friend = memberRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구입니다."));

        // 조회 후 없으면 생성
        ChatRoom chatRoom = chatRoomRepository.findPrivateChatRoomByParticipants(member.getId(), friend.getId()).stream()
                .findFirst()
                .orElseGet(() -> createPrivateChatRoom(member, friend));

        chatRoom.getParticipants().forEach(participant -> {
            if (participant.getStatus() == Status.INACTIVE) {
                participant.setStatus(Status.ACTIVE);
            }
        });

        return getChatRoomInfo(chatRoom.getId(), memberId );
    }
    // 개인 채팅방 생성
    public ChatRoom createPrivateChatRoom(
            Member member,
            Member friend
    ) {
        ChatRoom chatRoom = ChatRoom.builder()
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
                .lastReadMessageId(0L)  // 초기값
                .build();

        participantRepository.save(participant);
    }

    public void saveBandParticipant(ChatRoom chatRoom, Member member, Member Manager) {
        ChatRoomParticipant participantMember = ChatRoomParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .role(Role.MEMBER)  // 기본 역할 설정
                .status(Status.ACTIVE)  // 기본 상태 설정
                .lastReadMessageId(0L)  // 초기값
                .build();
        ChatRoomParticipant participantManager = ChatRoomParticipant.builder()
                .chatRoom(chatRoom)
                .member(Manager)
                .role(Role.BANDMANAGER)  // 기본 역할 설정
                .status(Status.ACTIVE)  // 기본 상태 설정
                .lastReadMessageId(0L)  // 초기값
                .build();
        List<ChatRoomParticipant> participants = List.of(participantMember, participantManager);

        participantRepository.saveAll(participants);
    }


    public ChatRoomListResponse getMyChatRooms(Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 3) 내가 매니저로 있는 모든 밴드
        List<Band> managedBands = bandRepository.findAllActiveByManagerId(memberId);

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
        // 그룹 채팅방 정보 생성
        List<ChatRoomInfoDto> groupChatRoomInfos = groupRooms.stream()
                .map(room ->{
                    List<MemberInfo> memberInfos = room.getParticipants().stream()
                            .filter(m -> !m.getMember().getId().equals(memberId))
                            .map(p -> MemberInfo.builder()
                                            .memberId(p.getMember().getId())
                                            .nickname(p.getMember().getNickname())
                                            .profileImageUrl(p.getMember().getProfileImageUrl())
                                            .lastReadMessageId(p.getLastReadMessageId())
                                            .build()
                                    )
                                    .toList();

                    LocalDateTime myPinnedAt = room.getParticipants().stream()
                            .filter(p -> p.getMember().getId().equals(memberId))
                            .findFirst()
                            .map(ChatRoomParticipant::getPinnedAt)
                            .orElse(null);


                    return ChatRoomInfoDto.builder()
                            .roomType(String.valueOf(RoomType.GROUP))
                            .roomId(room.getId())
                            .chatName(room.getName())
                            .imageUrl(room.getImageUrl())
                            .memberInfos(memberInfos)
                            .pinnedAt(myPinnedAt)
                            .unreadCount(unreadCountMap.get(room.getId()))
                            .lastMessageAt(lastAtMap.get(room.getId()))
                            .build();
                }).collect(Collectors.toList());


        // 개인 채팅방 정보 생성
        List<PrivateChatRoomInfoDto> privateChatRoomInfos = privateRooms.stream()
                .map(room ->{

                    MemberInfo memberInfo = room.getParticipants().stream()
                            .filter(m -> !m.getMember().getId().equals(memberId))
                            .map(p -> MemberInfo.builder()
                                    .memberId(p.getMember().getId())
                                    .nickname(p.getMember().getNickname())
                                    .profileImageUrl(p.getMember().getProfileImageUrl())
                                    .lastReadMessageId(p.getLastReadMessageId())
                                    .build())
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("내 정보가 없습니다."));

                    LocalDateTime myPinnedAt = room.getParticipants().stream()
                            .filter(p -> p.getMember().getId().equals(memberId))
                            .findFirst()
                            .map(ChatRoomParticipant::getPinnedAt)
                            .orElse(null);


                    return PrivateChatRoomInfoDto.builder()
                            .roomType(String.valueOf(RoomType.PRIVATE))
                            .roomId(room.getId())
                            .chatName(memberInfo.getNickname())
                            .imageUrl(memberInfo.getProfileImageUrl())
                            .memberInfo(memberInfo)
                            .pinnedAt(myPinnedAt)
                            .unreadCount(unreadCountMap.get(room.getId()))
                            .lastMessageAt(lastAtMap.get(room.getId()))
                            .build();
                }).collect(Collectors.toList());

        Map<Boolean, List<ChatRoom>> partitioned = bandRooms.stream()
                .collect(Collectors.partitioningBy(
                        room -> room.getParticipants().stream()
                                .anyMatch(p -> p.getMember().getId().equals(memberId)
                                        && p.getRole() == Role.BANDMANAGER)
                ));
        List<ChatRoom> myManagerRooms = partitioned.get(true);   // 내가 매니저인 방
        List<ChatRoom> myApplicantRooms = partitioned.get(false); // 내가 지원자인 방

        List<ChatRoomInfoDto> nonAdminBandRoomInfos = myApplicantRooms.stream()
                .map(room ->{
                    List<MemberInfo> memberInfos = room.getParticipants().stream()
                            .filter(p -> !p.getMember().getId().equals(memberId))
                            .map(p -> MemberInfo.builder()
                                    .memberId(p.getMember().getId())
                                    .nickname(p.getMember().getNickname())
                                    .profileImageUrl(p.getMember().getProfileImageUrl())
                                    .lastReadMessageId(p.getLastReadMessageId())
                                    .build()
                            )
                            .toList();


                    LocalDateTime myPinnedAt = room.getParticipants().stream()
                            .filter(m -> m.getMember().getId().equals(memberId))
                            .findFirst()
                            .map(ChatRoomParticipant::getPinnedAt)
                            .orElse(null);

                    return ChatRoomInfoDto.builder()
                            .roomType("BAND-APPLICANT")
                            .roomId(room.getId())
                            .chatName(room.getBandChat().getBand().getName())
                            .imageUrl(room.getBandChat().getBand().getProfileImageUrl())
                            .memberInfos(memberInfos)
                            .pinnedAt(myPinnedAt)
                            .unreadCount(unreadCountMap.get(room.getId()))
                            .lastMessageAt(lastAtMap.get(room.getId()))
                            .build();
                }).collect(Collectors.toList());


        Map<Long, List<ChatRoom>> adminRoomsByBandId = myManagerRooms.stream()
                .collect(Collectors.groupingBy(r -> r.getBandChat().getBand().getId()));

        List<BandManagerRoomInfoDto> allAdminBandRoomInfos = managedBands.stream()
                .map(band -> {
                    List<ChatRoom> rooms = adminRoomsByBandId.getOrDefault(band.getId(), Collections.emptyList());

                    // 세션 이름 목록(모집 중인 것만)
                    List<String> recruitingSessions = band.getBandSessions().stream()
                            .filter(s -> "RECRUITING".equals(s.getSessionStatus()))
                            .map(s -> s.getSession().getName())
                            .distinct()
                            .toList();


                    if (rooms.isEmpty()) {
                        // 채팅방이 하나도 없을 때
                        return BandManagerRoomInfoDto.builder()
                                .roomType("BAND-MANAGER")
                                .bandId(band.getId())
                                .bandName(band.getName())
                                .bandImageUrl(band.getProfileImageUrl())
                                .status(band.getStatus())
                                .bandSessionList(recruitingSessions)
                                .chatRoomInfo(Collections.emptyList())
                                .pinnedAt(band.getPinnedAt())
                                .unreadCount(0L)
                                .lastMessageAt(null)
                                .build();
                    }

                    List<BandManagerRoomInfoDto.BandChatRoomInfo> chatInfos = rooms.stream()
                            .map(room -> {
                                MemberInfo memberInfo = room.getParticipants().stream()
                                        .filter(m -> !m.getMember().getId().equals(memberId))
                                        .map(m -> MemberInfo.builder()
                                                .memberId(m.getMember().getId())
                                                .nickname(m.getMember().getNickname())
                                                .profileImageUrl(m.getMember().getProfileImageUrl())
                                                .lastReadMessageId(m.getLastReadMessageId())
                                                .build())
                                        .findFirst()
                                        .orElse(null);

                                LocalDateTime myPinnedAt = room.getParticipants().stream()
                                        .filter(p -> p.getMember().getId().equals(memberId))
                                        .findFirst()
                                        .map(ChatRoomParticipant::getPinnedAt)
                                        .orElse(null);

                                return BandManagerRoomInfoDto.BandChatRoomInfo.builder()
                                        .roomId(room.getId())
                                        .memberInfo(memberInfo)
                                        .pinnedAt( myPinnedAt)
                                        .session(room.getBandChat().getBandSession().getSession().getName())
                                        .passFail(room.getBandChat().getPassFail())
                                        .lastMessageAt(lastAtMap.get(room.getId()))
                                        .UnreadCount(unreadCountMap.get(room.getId()))
                                        .build();
                            })
                            .toList();

                    long totalUnread = chatInfos.stream()
                            .mapToLong(info -> Optional.ofNullable(info.getUnreadCount()).orElse(0L))
                            .sum();
                    LocalDateTime latest = chatInfos.stream()
                            .map(BandManagerRoomInfoDto.BandChatRoomInfo::getLastMessageAt)
                            .filter(Objects::nonNull)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);


                    return BandManagerRoomInfoDto.builder()
                            .roomType("BAND-MANAGER")
                            .bandId(band.getId())
                            .bandName(band.getName())
                            .bandImageUrl(band.getProfileImageUrl())
                            .status(band.getStatus())
                            .bandSessionList(recruitingSessions)
                            .chatRoomInfo(chatInfos)
                            .pinnedAt(band.getPinnedAt())
                            .unreadCount(totalUnread)
                            .lastMessageAt(latest)
                            .build();
                })
                .toList();


        List<ChatRoomInfo> combined = Stream.concat(Stream.concat(
                        Stream.concat(
                                privateChatRoomInfos.stream(),
                                nonAdminBandRoomInfos.stream()),
                                allAdminBandRoomInfos.stream()),
                        groupChatRoomInfos.stream())
                .toList();

        List<ChatRoomInfo> pinnedRooms = combined.stream()
                .filter(info -> info.getPinnedAt() != null)
                .sorted(Comparator.comparing(ChatRoomInfo::getPinnedAt).reversed())
                .toList();
        List<ChatRoomInfo> unpinnedRooms = combined.stream()
                .filter(info -> info.getPinnedAt() == null)
                .sorted(Comparator.comparing(
                        ChatRoomInfo::getLastMessageAt,
                        Comparator.nullsFirst(Comparator.reverseOrder())
                ))
                .toList();

        List<ChatRoomInfo> sorted = Stream.concat(pinnedRooms.stream(), unpinnedRooms.stream())
                .toList();
        return ChatRoomListResponse.builder()
                .chatRoomInfos(sorted)
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
    public BasicChatRoomInfo getChatRoomInfo(Long roomId, Long memberId){

        List<ChatRoomParticipant> participants
                = participantRepository.findAllActiveByChatRoomId(roomId);

        Member member = participants.stream()
                .map(ChatRoomParticipant::getMember)
                .filter(m -> m.getId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + memberId));

        List<ParticipantInfos.Info> infoList = new ArrayList<>();
        for(ChatRoomParticipant participant : participants){
            if(participant.getMember().getId().equals(member.getId())){
                participant.setLastReadAt(LocalDateTime.now());
                continue;
            }
            ParticipantInfos.Info info = ParticipantInfos.Info.builder()
                    .memberId(participant.getMember().getId())
                    .nickname(participant.getMember().getNickname())
                    .imageUrl(participant.getMember().getProfileImageUrl())
                    .lastReadMessageId(participant.getLastReadMessageId())
                    .build();
            infoList.add(info);
        }

        return BasicChatRoomInfo.builder()
                .roomId(roomId)
                .messageList(chatMessageService.getChatMessages(roomId,Long.MAX_VALUE,20))
                .participantInfos(
                        ParticipantInfos.builder()
                                .infos(infoList)
                                .build()
                        )
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
                .lastReadMessageId(0L)  // 초기값
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

    @Transactional
    public PinChatResponse pinChatRoom(Long roomId, Long memberId){

        ChatRoomParticipant participant = participantRepository.findByChatRoom_IdAndMember_IdAndStatus(
                roomId, memberId, Status.ACTIVE
        ).orElseThrow(() -> new IllegalArgumentException("해당 채팅방에 참여하지 않은 사용자입니다."));

        if (participant.getPinnedAt() == null) {
            participant.setPinnedAt(LocalDateTime.now());
        }

        return PinChatResponse.builder()
                .roomId(roomId)
                .pinnedAt(participant.getPinnedAt())
                .build();
    }

    @Transactional
    public PinBandChatRoomResponse pinBandChatRoom(Long bandId, Long memberId){

        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 밴드입니다. ID: " + bandId));
        if(!memberId.equals(band.getManager().getId())){
            throw new IllegalArgumentException("밴드 매니저만 핀할 수 있습니다.");
        }
        band.setPinnedAt(LocalDateTime.now());

        return PinBandChatRoomResponse.builder()
                .bandId(bandId)
                .pinnedAt(band.getPinnedAt())
                .build();
    }

    @Transactional
    public PinChatResponse unpinChatRoom(Long roomId, Long memberId){

        ChatRoomParticipant participant = participantRepository.findByChatRoom_IdAndMember_IdAndStatus(
                roomId, memberId, Status.ACTIVE
        ).orElseThrow(() -> new IllegalArgumentException("해당 채팅방에 참여하지 않은 사용자입니다."));

        if (participant.getPinnedAt() != null) {
            participant.setPinnedAt(null);
        }

        return PinChatResponse.builder()
                .roomId(roomId)
                .pinnedAt(participant.getPinnedAt())
                .build();
    }

    @Transactional
    public PinBandChatRoomResponse unpinBandChatRoom(Long bandId, Long memberId){

        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 밴드입니다. ID: " + bandId));
        if(!band.getManager().getId().equals(memberId)){
            throw new IllegalArgumentException("밴드 매니저만 핀할 수 있습니다.");
        }
        band.setPinnedAt(null);

        return PinBandChatRoomResponse.builder()
                .bandId(bandId)
                .pinnedAt(band.getPinnedAt())
                .build();
    }

    public void ChatRequest(Long targetId, Long memberId){

        boolean cn = chatNotificationRepository.existsBySenderIdAndReceiverIdAndIsRead(memberId, targetId, ReadStatus.UNREAD);

        if(cn){
            throw new IllegalArgumentException("이미 요청을 보냈습니다.");
        }

        Member sender = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + memberId));
        Member receiver = memberRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + targetId));


        Notification baseNotification = Notification.builder()
                .type(NotificationType.CHAT) //
                .isRead(ReadStatus.UNREAD)
                .sender(sender)
                .receiver(receiver)
                .build();
        notificationRepository.save(baseNotification);

        // FriendNotification 생성
        ChatNotification chatNotification = ChatNotification.builder()
                .notification(baseNotification)
                .sender(sender)
                .receiver(receiver)
                .isRead(ReadStatus.UNREAD)
                .build();
        chatNotificationRepository.save(chatNotification);
    }

}
