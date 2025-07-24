package com.umc.banddy.domain.band.bookmark.repository;

import com.umc.banddy.domain.band.bookmark.domain.mapping.BandBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.member.domain.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface BandBookmarkRepository extends JpaRepository<BandBookmark, Long> {

    List<BandBookmark> findByMember(Member member);

    Optional<BandBookmark> findByMemberAndBand(Member member, Band band);

    void deleteByMemberAndBand(Member member, Band band);

    boolean existsByMemberIdAndBandId(Long memberId, Long bandId);
}

