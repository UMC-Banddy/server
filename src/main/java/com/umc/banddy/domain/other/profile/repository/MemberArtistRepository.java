package com.umc.banddy.domain.other.profile.repository;

import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemberArtistRepository extends JpaRepository<MemberArtist, Long> {
    List<MemberArtist> findByMemberId(Long memberId);
}
