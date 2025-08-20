package com.umc.banddy.domain.music.artist.repository;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberArtistRepository extends JpaRepository<MemberArtist, Long> {

    Optional<MemberArtist> findByMemberAndArtist(Member member, Artist artist);

    List<MemberArtist> findAllByMember(Member member);

    void deleteByMemberAndArtist(Member member, Artist artist);

    List<MemberArtist> findByMemberId(Long memberId);


    // 내가 저장한 아티스트 ID 목록
    @Query("""
        SELECT ma.artist.id
        FROM MemberArtist ma
        WHERE ma.member.id = :memberId
    """)
    List<Long> findArtistIdsSavedByMember(@Param("memberId") Long memberId);

    // 본인 제외 조건 추가
    @Query("""
            SELECT ma.artist
              FROM MemberArtist ma
             WHERE ma.member IN :members
               AND ma.member.id <> :excludeMemberId
             GROUP BY ma.artist
             ORDER BY COUNT(ma.artist) DESC
            """)
    List<Artist> findTopSavedArtistsByMembers(
            @Param("members") List<Member> members,
            @Param("excludeMemberId") Long excludeMemberId,
            Pageable pageable
    );

    default List<Artist> findTopSavedArtistsByMembers(
            List<Member> members,
            Long excludeMemberId,
            int limit
    ) {
        List<Member> filtered = members.stream()
                .filter(m -> m.getId() != null && !m.getId().equals(excludeMemberId))
                .toList();

        if (filtered.isEmpty()) {
            return List.of();
        }
        return findTopSavedArtistsByMembers(filtered, excludeMemberId, Pageable.ofSize(limit));
    }
}
