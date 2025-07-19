package com.umc.banddy.domain.music.track.repository;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.music.track.domain.Track;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberTrackRepository extends JpaRepository<MemberTrack, Long> {

    Optional<MemberTrack> findByMemberAndTrack(Member member, Track track);

    void deleteByMemberAndTrack(Member member, Track track);

    List<MemberTrack> findAllByMember(Member member);

    List<MemberTrack> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    // 비슷한 유저들의 인기 트랙 조회
    @Query("SELECT mt.track FROM MemberTrack mt " +
            "WHERE mt.member IN :members " +
            "GROUP BY mt.track " +
            "ORDER BY COUNT(mt.track) DESC")
    List<Track> findTopSavedTracksByMembers(@Param("members") List<Member> members, Pageable pageable);

    default List<Track> findTopSavedTracksByMembers(List<Member> members, int limit) {
        return findTopSavedTracksByMembers(members, Pageable.ofSize(limit));
    }
}
