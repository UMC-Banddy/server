package com.umc.banddy.domain.band.profile.converter;

import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.*;
import com.umc.banddy.domain.band.profile.web.dto.BandProfileResponse;
import com.umc.banddy.domain.band.profile.web.dto.BandProfileResponse.*;
import com.umc.banddy.domain.band.profile.web.dto.BandDetailResponse;
import com.umc.banddy.domain.member.domain.Session;

import java.util.List;
import java.util.stream.Collectors;

public class BandProfileConverter {

    // 밴드 프로필
    public static BandProfileResponse toProfileResponse(
            Band band,
            List<BandTrack> goalTracks,
            List<BandArtist> preferredArtists,
            List<BandSns> sns
    ) {
        List<TrackDto> trackDtos = goalTracks.stream()
                .map(bt -> new TrackDto(
                        bt.getTrack().getTitle(),
                        bt.getTrack().getArtist(),
                        bt.getTrack().getImageUrl()))
                .collect(Collectors.toList());

        List<ArtistDto> artistDtos = preferredArtists.stream()
                .map(ba -> new ArtistDto(
                        ba.getArtist().getName(),
                        ba.getArtist().getImageUrl()))
                .collect(Collectors.toList());

        List<SnsDto> snsDtos = sns.stream()
                .map(s -> new SnsDto(s.getPlatform(), s.getSnsLink()))
                .collect(Collectors.toList());

        CompositionDto compositionDto = CompositionDto.builder()
                .averageAge(String.valueOf(band.getAverageAge()))
                .job(band.getJob())
                .maleCount(band.getMaleCount())
                .femaleCount(band.getFemaleCount())
                .sessions(band.getSessions().stream().map(Session::getName).collect(Collectors.toList()))
                .build();

        return BandProfileResponse.builder()
                .goalTracks(trackDtos)
                .preferredArtists(artistDtos)
                .composition(compositionDto)
                .sns(snsDtos)
                .build();
    }

    // 밴드 상세 응답
    public static BandDetailResponse toDetailResponse(
            Band band,
            boolean isBookmarked,
            List<BandSession> sessions,
            List<BandTag> tags,
            List<BandTrack> tracks
    ) {
        return BandDetailResponse.builder()
                .bandId(band.getId())
                .name(band.getName())
                .imageUrl(band.getProfileImageUrl())
                .description(band.getDescription())
                .isBookmarked(isBookmarked)
                .recruitingSessions(
                        sessions.stream()
                                .map(bs -> bs.getSession().getName())
                                .collect(Collectors.toList())
                )
                .tags(
                        tags.stream()
                                .map(bt -> bt.getTag().getName())
                                .collect(Collectors.toList())
                )
                .tracks(
                        tracks.stream()
                                .map(bt -> BandDetailResponse.TrackDto.builder()
                                        .trackId(bt.getTrack().getId())
                                        .title(bt.getTrack().getTitle())
                                        .artist(bt.getTrack().getArtist())
                                        .imageUrl(bt.getTrack().getImageUrl())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }
}
