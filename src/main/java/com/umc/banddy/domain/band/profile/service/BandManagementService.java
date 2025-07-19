package com.umc.banddy.domain.band.profile.service;

import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.*;
import com.umc.banddy.domain.band.profile.enums.BandStatus;
import com.umc.banddy.domain.band.profile.enums.Gender;
import com.umc.banddy.domain.band.profile.repository.*;
import com.umc.banddy.domain.chat.web.dto.RecruitmentRequest;
import com.umc.banddy.domain.chat.web.dto.RecruitmentResponse;
import com.umc.banddy.domain.member.domain.Genre;
import com.umc.banddy.domain.member.domain.Session;
import com.umc.banddy.domain.member.repository.GenreRepository;
import com.umc.banddy.domain.member.repository.SessionRepository;
import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.music.artist.repository.ArtistRepository;
import com.umc.banddy.domain.music.track.domain.Track;
import com.umc.banddy.domain.music.track.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BandManagementService {

    private final BandRepository bandRepository;

    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;
    private final SessionRepository sessionRepository;
    private final GenreRepository genreRepository;

    private final BandSessionRepository bandSessionRepository;
    private final BandGenreRepository bandGenreRepository;
    private final BandArtistRepository bandArtistRepository;
    private final BandTrackRepository bandTrackRepository;
    private final BandJobRepository bandJobRepository;
    private final BandSnsRepository bandSnsRepository;


    public RecruitmentResponse createRecruitment(RecruitmentRequest request){

        String representativeSong = trackRepository.findBySpotifyId(request.getRepresentativeSong())
                .orElseThrow(() -> new IllegalArgumentException("곡이 존재하지 않습니다."))
                .getTitle();

        Band band = Band.builder()
                .status(request.getStatus())
                .profileImageUrl(request.getProfileImageUrl())
                .representativeSong(representativeSong)
                .name(request.getName())
                .description(request.getDescription())
                .endDate(request.getEndDate())
                .ageStart(request.getAgeStart())
                .ageEnd(request.getAgeEnd())
                .gender(Gender.valueOf(request.getGender().toUpperCase()))
                .region(request.getRegion())
                .district(request.getDistrict())
                .status(BandStatus.RECRUITING)
                .averageAge(request.getAverageAge())
                .build();

        Band savedBand = bandRepository.save(band);

        // 모집 세션 저장(모집 세션 + 현재 세션)
        List<String> mergedList = Stream
                .concat(request.getSession().stream(), request.getCurrentSessions().stream())
                .distinct()
                .toList();

        List<Session> SessionAll = sessionRepository.findByNameIn(mergedList);
        Map<String, Session> sessionMap = SessionAll.stream()
                .collect(Collectors.toMap(Session::getName, Function.identity()));

        List<BandSession> sessions = request.getSession().stream()
                .map(name -> {
                    Session s = sessionMap.get(name);
                    if (s == null) throw new IllegalArgumentException("세션이 존재하지 않습니다: " + name);
                    return BandSession.builder()
                            .band(savedBand)
                            .session(s)
                            .sessionStatus("RECRUITING")
                            .build();
                })
                .toList();

        List<BandSession> currentSessions = request.getCurrentSessions().stream()
                .map(name -> {
                    Session s = sessionMap.get(name);
                    if (s == null) throw new IllegalArgumentException("세션이 존재하지 않습니다: " + name);
                    return BandSession.builder()
                            .band(savedBand)
                            .session(s)
                            .sessionStatus("PARTICIPATING")
                            .build();
                })
                .toList();

        List<BandSession> mergedSession = Stream
                .concat(sessions.stream(), currentSessions.stream())
                .distinct()
                .toList();

        // 밴드 장르 저장
        List<Genre> GenreAll = genreRepository.findByIdIn(request.getGenre());
        Map<Long, Genre> genreMap = GenreAll.stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));

        List<BandGenre> genres = request.getGenre().stream()
                .map(genreId -> {
                    Genre g = genreMap.get(genreId);
                    if (g == null) throw new IllegalArgumentException("장르이 존재하지 않습니다: " + genreId );
                return BandGenre.builder()
                            .band(savedBand)
                            .genre(g)
                            .build();
                }).toList();


        // 밴드 아티스트 저장
        List<Artist> ArtistAll = artistRepository.findByIdIn(request.getArtist());
        Map<Long, Artist> artistMap = ArtistAll.stream()
                .collect(Collectors.toMap(Artist::getId, Function.identity()));

        List<BandArtist> artists = request.getArtist().stream()
                .map(artistId -> {
                    Artist a = artistMap.get(artistId);
                    if (a == null) throw new IllegalArgumentException("아티스트가 존재하지 않습니다: " + artistId );
                    return BandArtist.builder()
                            .band(savedBand)
                            .artist(a)
                            .build();
                }).toList();

        // 밴드 트랙 저장
        List<Track> TrackAll = trackRepository.findByIdIn(request.getTrack());
        Map<Long, Track> trackMap = TrackAll.stream()
                .collect(Collectors.toMap(Track::getId, Function.identity()));

        List<BandTrack> tracks = request.getTrack().stream()
                .map(trackId -> {
                    Track t = trackMap.get(trackId);
                    if (t == null) throw new IllegalArgumentException("노래가 존재하지 않습니다: " + trackId );
                    return BandTrack.builder()
                            .band(savedBand)
                            .track(t)
                            .build();
                }).toList();

        // 직업 저장
        List<BandJob> jobs = request.getJob().stream()
                .map(job -> BandJob.builder()
                        .band(savedBand)
                        .job(job)
                        .build())
                .toList();

        // sns 저장
        List<BandSns> snsLinks = request.getSnsLinks().entrySet().stream()
                .map(entry -> BandSns.builder()
                        .band(savedBand)
                        .platform(entry.getKey())
                        .snsLink(entry.getValue())
                        .build())
                .toList();

        bandSessionRepository.saveAll(mergedSession);
        bandGenreRepository.saveAll(genres);
        bandArtistRepository.saveAll(artists);
        bandTrackRepository.saveAll(tracks);
        bandJobRepository.saveAll(jobs);
        bandSnsRepository.saveAll(snsLinks);


        return RecruitmentResponse.builder()
                .bandId(savedBand.getId())
                .bandName(savedBand.getName())
                .profileImageUrl(savedBand.getProfileImageUrl())
                .type("Manager")
                .createdAt(savedBand.getCreatedAt())
                .build();

    }

}
