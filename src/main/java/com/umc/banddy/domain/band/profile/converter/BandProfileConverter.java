package com.umc.banddy.domain.band.profile.converter;

import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.*;
import com.umc.banddy.domain.band.profile.web.dto.BandProfileResponse;
import com.umc.banddy.domain.band.profile.web.dto.BandProfileResponse.*;
import com.umc.banddy.domain.band.profile.web.dto.BandDetailResponse;

import java.util.List;
import java.util.stream.Collectors;

public class BandProfileConverter {

    // 밴드 프로필
    public static BandProfileResponse toProfileResponse(
            Band band,
            List<BandTrack> goalTracks,
            List<BandArtist> preferredArtists,
            List<BandSns> sns,
            List<BandSession> sessions,
            List<BandJob> jobs
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

        List<String> sessionList = sessions.stream()
                .map(bs -> bs.getSession().getName())
                .collect(Collectors.toList());

        List<String> jobList = jobs.stream()
                .map(BandJob::getJob)
                .collect(Collectors.toList());

        CompositionDto compositionDto = CompositionDto.builder()
                .averageAge(
                        band.getAverageAge() != null ? String.valueOf(band.getAverageAge()) : "정보 없음"
                )
                .maleCount(band.getMaleCount() != null ? band.getMaleCount() : 0)
                .femaleCount(band.getFemaleCount() != null ? band.getFemaleCount() : 0)
                .build();

        return BandProfileResponse.builder()
                .goalTracks(trackDtos)
                .preferredArtists(artistDtos)
                .composition(compositionDto)
                .sns(snsDtos)
                .sessions(sessionList) // 밴드에 존재하는 세션, 따로 DTO로 받음
                .jobs(jobList)
                .build();
    }

    // 밴드 상세 응답
    public static BandDetailResponse toDetailResponse(
            Band band,
            List<BandSns> snsList
    ) {
        String ageRange = (band.getAgeStart() != null)
                ? band.getAgeStart() + "대 이상"
                : "연령 무관";

        String gender = switch (band.getGender()) {
            case MALE -> "남성만";
            case FEMALE -> "여성만";
            case OTHER -> "성별 무관";
            default -> "미지정";
        };

        String region = band.getRegion();
        if (band.getDistrict() != null && !band.getDistrict().isEmpty()) {
            region += " " + band.getDistrict();
        }

        List<BandDetailResponse.SnsDto> snsDtoList = snsList.stream()
                .map(sns -> BandDetailResponse.SnsDto.builder()
                        .platform(sns.getPlatform())
                        .snsLink(sns.getSnsLink())
                        .build())
                .collect(Collectors.toList());

        return BandDetailResponse.builder()
                .bandId(band.getId())
                .bandName(band.getName())
                .profileImageUrl(band.getProfileImageUrl())
                .description(band.getDescription())
                .ageRange(ageRange)
                .genderCondition(gender)
                .region(region)
                .endDate(band.getEndDate() != null
                        ? band.getEndDate().toLocalDate().toString().replace("-", ".") // yy.MM.dd 포맷
                        : null)
                .snsList(snsDtoList)
                .build();
    }

}
