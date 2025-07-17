package com.umc.banddy.domain.band.profile.converter;

import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.BandArtist;
import com.umc.banddy.domain.band.profile.domain.mapping.BandSns;
import com.umc.banddy.domain.band.profile.domain.mapping.BandTrack;
import com.umc.banddy.domain.band.profile.web.dto.BandProfileResponse;
import com.umc.banddy.domain.band.profile.web.dto.BandProfileResponse.*;
import com.umc.banddy.domain.member.domain.Session;

import java.util.List;
import java.util.stream.Collectors;

public class BandProfileConverter {

    public static BandProfileResponse toResponse(
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
}
