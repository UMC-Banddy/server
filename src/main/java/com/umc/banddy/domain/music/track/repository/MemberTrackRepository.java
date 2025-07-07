package com.umc.banddy.domain.music.track.repository;

import com.umc.banddy.domain.music.Member;
import com.umc.banddy.domain.music.track.domain.Track;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberTrackRepository extends JpaRepository<MemberTrack, Long> {
    Optional<MemberTrack> findByMemberAndTrack(Member member, Track track);
    void deleteByMemberAndTrack(Member member, Track track);
    List<MemberTrack> findAllByMember(Member member);

}
