package com.umc.banddy.domain.friend.service;

import com.umc.banddy.domain.friend.converter.FriendConverter;
import com.umc.banddy.domain.friend.domain.Friend;
import com.umc.banddy.domain.friend.repository.FriendRepository;
import com.umc.banddy.domain.friend.web.dto.FriendResponseDto;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void deleteFriend(Long friendId) {
        friendRepository.deleteById(friendId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponseDto> getMyFriends(Long memberId) {
        List<Friend> friends = friendRepository.findAll().stream()
                .filter(friend -> friend.getMemberId().equals(memberId) || friend.getFriendshipId().equals(memberId))
                .collect(Collectors.toList());

        return friends.stream()
                .map(friend -> {
                    Long otherId = friend.getMemberId().equals(memberId)
                            ? friend.getFriendshipId()
                            : friend.getMemberId();

                    Member other = memberRepository.findById(otherId)
                            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

                    return FriendConverter.toDto(friend, other);
                })
                .collect(Collectors.toList());
    }
}
