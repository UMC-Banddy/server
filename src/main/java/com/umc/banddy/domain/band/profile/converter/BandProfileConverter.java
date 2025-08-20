package com.umc.banddy.domain.band.profile.converter;

import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.*;
import com.umc.banddy.domain.band.profile.web.dto.BandProfileResponse;
import com.umc.banddy.domain.band.profile.web.dto.BandProfileResponse.*;
import com.umc.banddy.domain.band.profile.web.dto.BandDetailResponse;
import com.umc.banddy.domain.band.profile.web.dto.BandSuggestionResponse;

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
        // 연령 조건 계산
        String ageRange;
        if (band.getAgeStart() != null && band.getAgeEnd() != null) {
            int startDecade = (band.getAgeStart() / 10) * 10;
            int endDecade = (band.getAgeEnd() / 10) * 10;

            if (startDecade == endDecade) {
                ageRange = startDecade + "대 이상";
            } else {
                ageRange = startDecade + "대 이상 - " + endDecade + "대 이하";
            }
        } else if (band.getAgeStart() != null) {
            int startDecade = (band.getAgeStart() / 10) * 10;
            ageRange = startDecade + "대 이상";
        } else if (band.getAgeEnd() != null) {
            int endDecade = (band.getAgeEnd() / 10) * 10;
            ageRange = endDecade + "대 이하";
        } else {
            ageRange = "연령 무관";
        }


        String gender = switch (band.getGender()) {
            case MALE -> "남성만";
            case FEMALE -> "여성만";
            case OTHER -> "성별 무관";
            default -> "미지정";
        };

        String region = band.getRegion();
        String district = band.getDistrict();

        // SNS 변환
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
                .district(district)
                .endDate(band.getEndDate() != null
                        ? band.getEndDate().toLocalDate().toString().replace("-", ".")
                        : null)
                .snsList(snsDtoList)
                .build();
    }

    public static BandSuggestionResponse toSuggestionResponse(List<BandArtist> preferredArtists) {
        if (preferredArtists == null || preferredArtists.isEmpty()) {
            return new BandSuggestionResponse("밴드 취향에 맞는 곡은 어때요?", null, null);
        }

        var artist = preferredArtists.get(0).getArtist();
        String artistName = artist.getName();

        String genre = null;
        try {
            Object g = artist.getClass().getMethod("getGenre").invoke(artist);
            genre = (g != null) ? g.toString() : null;
        } catch (Exception ignore) {
        }

        String suggestion = (genre != null && !genre.isBlank())
                ? String.format("%s 장르의 %s의 곡은 어때요?", genre, artistName)
                : String.format("%s의 곡은 어때요?", artistName);

        return new BandSuggestionResponse(suggestion, genre, artistName);
    }

    public static BandSuggestionResponse toStaticSuggestion() {
        return new BandSuggestionResponse("밴드에 맞는 곡을 찾아볼까요?", null, null);
    }
}
