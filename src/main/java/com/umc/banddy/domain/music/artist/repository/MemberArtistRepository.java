package com.umc.banddy.domain.music.artist.repository;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberArtistRepository extends JpaRepository<MemberArtist, Long> {
    Optional<MemberArtist> findByMemberAndArtist(Member member, Artist artist);
    List<MemberArtist> findAllByMember(Member member);
    void deleteByMemberAndArtist(Member member, Artist artist);
}
