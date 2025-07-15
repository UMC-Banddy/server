package com.umc.banddy.domain.band.preference.repository;

import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MemberPreferenceRepository extends Repository<MemberTrack, Long> {

    @Query("SELECT mt.tag FROM MemberTag mt WHERE mt.member.id = :memberId")
    List<String> findPreferredTags(@Param("memberId") Long memberId);

    @Query("SELECT a.name FROM MemberArtist ma JOIN ma.artist a WHERE ma.member.id = :memberId")
    List<String> findPreferredArtists(@Param("memberId") Long memberId);

    @Query("SELECT t.title FROM MemberTrack mt JOIN mt.track t WHERE mt.member.id = :memberId")
    List<String> findPreferredTracks(@Param("memberId") Long memberId);
}

