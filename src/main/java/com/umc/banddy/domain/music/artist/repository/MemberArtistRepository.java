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

    @Query("SELECT ma.artist FROM MemberArtist ma " +
            "WHERE ma.member IN :members " +
            "AND ma.member.id <> :excludeMemberId " +
            "GROUP BY ma.artist " +
            "ORDER BY COUNT(ma.artist) DESC")
    List<Artist> findTopSavedArtistsByMembers(
            @Param("members") List<Member> members,
            @Param("excludeMemberId") Long excludeMemberId,
            Pageable pageable);

    default List<Artist> findTopSavedArtistsByMembers(
            List<Member> members,
            Long excludeMemberId,
            int limit) {
        return findTopSavedArtistsByMembers(members, excludeMemberId, Pageable.ofSize(limit));
    }

}
