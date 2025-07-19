package com.umc.banddy.domain.friend.service;

import com.umc.banddy.domain.friend.domain.Friend;
import com.umc.banddy.domain.friend.domain.FriendStatus;
import com.umc.banddy.domain.friend.web.dto.FriendResponseDto;
import com.umc.banddy.domain.friend.repository.FriendRepository;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void requestFriend(Long requesterId, Long receiverId) {
        if (friendRepository.findByMemberIdAndFriendshipId(requesterId, receiverId).isPresent()) {
            throw new IllegalStateException("이미 친구 요청을 보냈습니다.");
        }

        Friend friend = Friend.builder()
                .memberId(requesterId)
                .friendshipId(receiverId)
                .status(FriendStatus.REQUESTED)
                .build();

        friendRepository.save(friend);
    }

    @Override
    @Transactional
    public void acceptFriend(Long friendId) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청이 존재하지 않습니다."));

        friend.setStatus(FriendStatus.ACCEPTED);
    }

    @Override
    @Transactional
    public void rejectFriend(Long friendId) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청이 존재하지 않습니다."));

        friend.setStatus(FriendStatus.REJECTED);
    }

    @Override
    @Transactional
    public void deleteFriend(Long friendId) {
        friendRepository.deleteById(friendId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponseDto> getMyFriends(Long memberId) {
        List<Friend> friends = friendRepository.findAcceptedFriends(memberId, FriendStatus.ACCEPTED);

        return friends.stream()
                .map(friend -> {
                    Long otherId = friend.getMemberId().equals(memberId)
                            ? friend.getFriendshipId()
                            : friend.getMemberId();

                    Member other = memberRepository.findById(otherId)
                            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

                    return FriendResponseDto.builder()
                            .friendId(other.getId())
                            .nickname(other.getNickname())
                            .email(other.getEmail())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponseDto> getReceivedFriendRequests(Long memberId) {
        List<Friend> requests = friendRepository.findByFriendshipIdAndStatus(memberId, FriendStatus.REQUESTED);

        return requests.stream()
                .map(friend -> {
                    Member requester = memberRepository.findById(friend.getMemberId())
                            .orElseThrow(() -> new IllegalArgumentException("요청한 회원이 존재하지 않습니다."));

                    return FriendResponseDto.builder()
                            .friendId(friend.getId())
                            .nickname(requester.getNickname())
                            .email(requester.getEmail())
                            .build();
                })
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public FriendResponseDto getFriendRequestDetail(Long friendId) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청이 존재하지 않습니다."));

        Member requester = memberRepository.findById(friend.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("요청한 회원이 존재하지 않습니다."));

        return FriendResponseDto.builder()
                .friendId(friend.getId())
                .nickname(requester.getNickname())
                .email(requester.getEmail())
                .build();
    }

}
