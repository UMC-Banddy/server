package com.umc.banddy.domain.friend.service;

import com.umc.banddy.domain.friend.domain.Friend;
import com.umc.banddy.domain.friend.domain.FriendRequest;
import com.umc.banddy.domain.friend.domain.FriendStatus;
import com.umc.banddy.domain.friend.repository.FriendRepository;
import com.umc.banddy.domain.friend.repository.FriendRequestRepository;
import com.umc.banddy.domain.friend.web.dto.FriendRequestResponseDto;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.mypage.notification.domain.Notification;
import com.umc.banddy.domain.mypage.notification.enums.NotificationType;
import com.umc.banddy.domain.mypage.notification.enums.ReadStatus;
import com.umc.banddy.domain.mypage.notification.repository.NotificationRepository;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umc.banddy.domain.mypage.notification.domain.mapping.FriendNotification;
import com.umc.banddy.domain.mypage.notification.repository.FriendNotificationRepository;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final FriendNotificationRepository friendNotificationRepository;

    @Override
    @Transactional
    public void requestFriend(Long requesterId, Long receiverId, String message) {
        Optional<FriendRequest> existing = friendRequestRepository
                .findTopByRequesterIdAndReceiverIdOrderByCreatedAtDesc(requesterId, receiverId);

        if (existing.isPresent()) {
            FriendStatus status = existing.get().getStatus();
            if (status == FriendStatus.REQUESTED) {
                throw new GeneralException(ErrorStatus.FRIEND_REQUEST_ALREADY_SENT);
            }
            if (status == FriendStatus.REJECTED) {
                // 재요청 가능
            }
        }

        FriendRequest request = FriendRequest.builder()
                .requesterId(requesterId)
                .receiverId(receiverId)
                .status(FriendStatus.REQUESTED)
                .build();

        friendRequestRepository.save(request);

        Member sender = memberRepository.findById(requesterId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_SENDER_NOT_FOUND));
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_RECEIVER_NOT_FOUND));

        Notification baseNotification = Notification.builder()
                .type(NotificationType.FRIEND) //
                .isRead(ReadStatus.UNREAD)
                .sender(sender)
                .receiver(receiver)
                .build();
        notificationRepository.save(baseNotification);

        // FriendNotification 생성
        FriendNotification friendNotification = FriendNotification.builder()
                .notification(baseNotification)
                .friendRequest(request)
                .type("REQUEST")
                .message(message)
                .build();
        friendNotificationRepository.save(friendNotification);
    }

    @Override
    @Transactional
    public void acceptFriend(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_REQUEST_NOT_FOUND));

        if (request.getStatus() != FriendStatus.REQUESTED) {
            throw new GeneralException(ErrorStatus.FRIEND_REQUEST_ALREADY_HANDLED);
        }

        request.setStatus(FriendStatus.ACCEPTED);

        Friend friend = Friend.builder()
                .memberId(request.getRequesterId())
                .friendshipId(request.getReceiverId())
                .build();

        friendRepository.save(friend);
        // 친구 요청 삭제
        friendNotificationRepository.deleteByFriendRequestIdAndType(requestId, "REQUEST");
    }

    @Override
    @Transactional
    public void rejectFriend(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_REQUEST_NOT_FOUND));
        request.setStatus(FriendStatus.REJECTED);
        friendNotificationRepository.deleteByFriendRequestIdAndType(requestId, "REQUEST"); //
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRequestResponseDto> getReceivedFriendRequests(Long memberId) {
        List<FriendRequest> requests = friendRequestRepository.findByReceiverIdAndStatus(memberId, FriendStatus.REQUESTED);

        return requests.stream()
                .map(request -> {
                    Member requester = memberRepository.findById(request.getRequesterId())
                            .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_SENDER_NOT_FOUND));

                    return FriendRequestResponseDto.builder()
                            .requestId(request.getId())
                            .otherMemberId(requester.getId())
                            .nickname(requester.getNickname())
                            .email(requester.getEmail())
                            .bio(requester.getBio())
                            .profileImageUrl(requester.getProfileImageUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FriendRequestResponseDto getFriendRequestDetail(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_REQUEST_NOT_FOUND));

        Member requester = memberRepository.findById(request.getRequesterId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_SENDER_NOT_FOUND));

        return FriendRequestResponseDto.builder()
                .requestId(request.getId())
                .otherMemberId(requester.getId())
                .nickname(requester.getNickname())
                .email(requester.getEmail())
                .bio(requester.getBio())
                .profileImageUrl(requester.getProfileImageUrl())
                .build();
    }
}
