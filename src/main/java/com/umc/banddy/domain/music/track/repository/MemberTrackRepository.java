package com.umc.banddy.domain.music.track.repository;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.music.track.domain.Track;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberTrackRepository extends JpaRepository<MemberTrack, Long> {

    Optional<MemberTrack> findByMemberAndTrack(Member member, Track track);

    void deleteByMemberAndTrack(Member member, Track track);

    List<MemberTrack> findAllByMember(Member member);

    List<MemberTrack> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    List<MemberTrack> findByMemberIdIn(Collection<Long> memberIds);

    /**
     * 비슷한 유저들이 저장했지만 내가 저장하지 않은 트랙을
     * 저장 수(인기순) 내림차순으로 TOP N 반환 (members 버전)
     */
    @Query("""
        select t
          from MemberTrack mt
          join mt.track t
         where mt.member in :members
           and mt.member.id <> :loginMemberId
           and t.id not in (
                 select mt2.track.id
                   from MemberTrack mt2
                  where mt2.member.id = :loginMemberId
           )
         group by t.id
         order by count(mt.id) desc
    """)
    List<Track> findTopSavedTracksByMembers(
            @Param("members") List<Member> members,
            @Param("loginMemberId") Long loginMemberId,
            Pageable pageable
    );

    /**
     * 비슷한 유저들이 저장했지만 내가 저장하지 않은 트랙을
     * 저장 수(인기순) 내림차순으로 TOP N 반환 (memberIds 버전)
     * - similarityUtil이 id 리스트만 줄 때 사용
     */
    @Query("""
        select t
          from MemberTrack mt
          join mt.track t
         where mt.member.id in :memberIds
           and t.id not in (
                 select mt2.track.id
                   from MemberTrack mt2
                  where mt2.member.id = :loginMemberId
           )
         group by t.id
         order by count(mt.id) desc
    """)
    List<Track> findTopSavedTracksByMemberIds(
            @Param("memberIds") List<Long> memberIds,
            @Param("loginMemberId") Long loginMemberId,
            Pageable pageable
    );

    /**
     * (옵션) 내 저장곡과 최소 n곡 이상 겹치는 유저 id 상위 K (겹친 트랙 수 내림차순)
     * - fallback 유사도 계산용
     */
    @Query("""
        select mt.member.id
          from MemberTrack mt
         where mt.track.id in (
               select mt2.track.id
                 from MemberTrack mt2
                where mt2.member.id = :loginMemberId
         )
           and mt.member.id <> :loginMemberId
         group by mt.member.id
        having count(distinct mt.track.id) >= :minOverlap
         order by count(distinct mt.track.id) desc
    """)
    List<Long> findSimilarMemberIdsByTrackOverlap(
            @Param("loginMemberId") Long loginMemberId,
            @Param("minOverlap") long minOverlap,
            Pageable pageable
    );
}
