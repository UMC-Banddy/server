package com.umc.banddy.domain.friend.service;

import com.umc.banddy.domain.friend.domain.Friend;
import com.umc.banddy.domain.friend.domain.FriendRequest;
import com.umc.banddy.domain.friend.domain.FriendStatus;
import com.umc.banddy.domain.friend.repository.FriendRepository;
import com.umc.banddy.domain.friend.repository.FriendRequestRepository;
import com.umc.banddy.domain.friend.web.dto.FriendRequestResponseDto;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.mypage.notification.enums.ReadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umc.banddy.domain.mypage.notification.domain.mapping.FriendNotification;
import com.umc.banddy.domain.mypage.notification.repository.FriendNotificationRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final FriendNotificationRepository friendNotificationRepository;

    @Override
    @Transactional
    public void requestFriend(Long requesterId, Long receiverId) {
        Optional<FriendRequest> existing = friendRequestRepository
                .findTopByRequesterIdAndReceiverIdOrderByCreatedAtDesc(requesterId, receiverId);

        if (existing.isPresent()) {
            FriendStatus status = existing.get().getStatus();
            if (status == FriendStatus.REQUESTED) {
                throw new IllegalStateException("이미 친구 요청을 보냈습니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("보낸 회원 없음"));
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("받는 회원 없음"));

        FriendNotification notification = FriendNotification.builder()
                .sender(sender)
                .receiver(receiver)
                .friendRequest(request)
                .isRead(ReadStatus.UNREAD)
                .type("REQUEST")
                .build();

        friendNotificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void acceptFriend(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청이 존재하지 않습니다."));

        if (request.getStatus() != FriendStatus.REQUESTED) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("친구 요청이 존재하지 않습니다."));
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
                            .orElseThrow(() -> new IllegalArgumentException("요청한 회원이 존재하지 않습니다."));

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
                .orElseThrow(() -> new IllegalArgumentException("친구 요청이 존재하지 않습니다."));

        Member requester = memberRepository.findById(request.getRequesterId())
                .orElseThrow(() -> new IllegalArgumentException("요청한 회원이 존재하지 않습니다."));

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
