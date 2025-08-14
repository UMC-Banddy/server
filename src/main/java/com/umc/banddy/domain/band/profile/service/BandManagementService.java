package com.umc.banddy.domain.band.profile.service;

import com.umc.banddy.domain.band.profile.domain.Band;
import com.umc.banddy.domain.band.profile.domain.mapping.*;
import com.umc.banddy.domain.band.profile.enums.BandStatus;
import com.umc.banddy.domain.band.profile.enums.Gender;
import com.umc.banddy.domain.band.profile.repository.*;
import com.umc.banddy.domain.band.profile.web.dto.Recruitment.*;
import com.umc.banddy.domain.chat.domain.ChatMessage;
import com.umc.banddy.domain.chat.domain.ChatRoom;
import com.umc.banddy.domain.chat.domain.ChatRoomParticipant;
import com.umc.banddy.domain.chat.domain.enums.PassFail;
import com.umc.banddy.domain.chat.domain.enums.Role;
import com.umc.banddy.domain.chat.domain.enums.RoomType;
import com.umc.banddy.domain.chat.repository.ChatMessageRepository;
import com.umc.banddy.domain.chat.repository.ChatRoomRepository;
import com.umc.banddy.domain.chat.service.ChatRoomService;
import com.umc.banddy.domain.chat.web.dto.chatroom.BasicChatRoomInfo;
import com.umc.banddy.domain.member.domain.Genre;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.domain.Session;
import com.umc.banddy.domain.member.repository.GenreRepository;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.member.repository.SessionRepository;
import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.music.artist.repository.ArtistRepository;
import com.umc.banddy.domain.music.artist.service.ArtistService;
import com.umc.banddy.domain.music.track.domain.Track;
import com.umc.banddy.domain.music.track.repository.TrackRepository;
import com.umc.banddy.domain.music.track.service.TrackService;
import com.umc.banddy.global.apiPayload.code.status.ErrorStatus;
import com.umc.banddy.global.apiPayload.exception.GeneralException;
import com.umc.banddy.global.infra.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class BandManagementService {

    private final MemberRepository memberRepository;
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

    private final ChatRoomService chatRoomService;
    private final ChatRoomRepository chatRoomRepository;
    private final BandChatRepository bandChatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final S3Uploader s3Uploader;

    private final ArtistService artistService;
    private final TrackService trackService;


    @Transactional
    public RecruitmentResponse createRecruitment(RecruitmentRequest request, MultipartFile image, Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        String profileImageUrl = (image != null && !image.isEmpty())
                ? s3Uploader.upload(image, "band-profile-images") : null;

        Track track = trackRepository.findBySpotifyId(request.getRepresentativeSong())
                .orElseThrow(() -> new GeneralException(ErrorStatus.TRACK_NOT_FOUND));

        Band band = Band.builder()
                .status(BandStatus.RECRUITING)
                .profileImageUrl(profileImageUrl)
                .representativeSong(null) // 일단 null로 설정
                .representativeTrack(track)
                .name(request.getName())
                .description(request.getDescription())
                .endDate(request.getEndDate())
                .autoClose(request.getAutoClose())
                .ageStart(request.getAgeStart())
                .ageEnd(request.getAgeEnd())
                .gender(Gender.valueOf(request.getGender().toUpperCase()))
                .region(request.getRegion())
                .district(request.getDistrict())
                .maleCount(request.getMaleCount())
                .femaleCount(request.getFemaleCount())
                .averageAge(request.getAverageAge())
                .manager(member)
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
                    if (s == null) {
                        throw new GeneralException(ErrorStatus.SESSION_NOT_FOUND);
                    }
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
                    if (s == null) {
                        throw new GeneralException(ErrorStatus.SESSION_NOT_FOUND);
                    }
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
        List<Genre> GenreAll = genreRepository.findByNameIn(request.getGenres());
        Map<String, Genre> genreMap = GenreAll.stream()
                .collect(Collectors.toMap(Genre::getName, Function.identity()));

        List<BandGenre> genres = request.getGenres().stream()
                .map(genreName -> {
                    Genre g = genreMap.get(genreName);
                    if (g == null) throw new GeneralException(ErrorStatus.GENRE_NOT_FOUND);
                return BandGenre.builder()
                            .band(savedBand)
                            .genre(g)
                            .build();
                }).toList();


        // 아티스트 저장 (없으면 조회 후 저장)
        List<Artist> artistAll = artistRepository.findBySpotifyIdIn(request.getArtistSpotifyIds());
        Set<String> existingArtistIds = artistAll.stream().map(Artist::getSpotifyId).collect(Collectors.toSet());
        List<String> missingArtistIds = request.getArtistSpotifyIds().stream()
                .filter(id -> !existingArtistIds.contains(id))
                .toList();
        if (!missingArtistIds.isEmpty()) {
            List<Artist> newArtists = artistService.saveArtistsBySpotifyIds(missingArtistIds);
            artistAll = Stream.concat(artistAll.stream(), newArtists.stream()).toList();

        }

        Map<String, Artist> artistMap = artistAll.stream()
                .collect(Collectors.toMap(Artist::getSpotifyId, Function.identity()));

        List<BandArtist> artists = request.getArtistSpotifyIds().stream()
                .map(artistId -> {
                    Artist a = artistMap.get(artistId);
                    if (a == null) throw new GeneralException(ErrorStatus.ARTIST_NOT_FOUND);
                    return BandArtist.builder()
                            .band(savedBand)
                            .artist(a)
                            .build();
                }).toList();

        // 트랙 저장 (없으면 조회 후 저장)
        List<Track> TrackAll = trackRepository.findBySpotifyIdIn(request.getTrackSpotifyIds());
        Set<String> existingTrackIds = TrackAll.stream().map(Track::getSpotifyId).collect(Collectors.toSet());
        List<String> missingTrackIds = request.getTrackSpotifyIds().stream()
                .filter(id -> !existingTrackIds.contains(id))
                .toList();
        if (!missingTrackIds.isEmpty()) {
            List<Track> newTracks = trackService.saveTracksBySpotifyIds(missingTrackIds);
            TrackAll = Stream.concat(TrackAll.stream(), newTracks.stream()).toList();
        }
        Map<String, Track> trackMap = TrackAll.stream()
                .collect(Collectors.toMap(Track::getSpotifyId, Function.identity()));
        List<BandTrack> tracks = request.getTrackSpotifyIds().stream()
                .map(trackId -> {
                    Track t = trackMap.get(trackId);
                    if (t == null) throw new GeneralException(ErrorStatus.TRACK_NOT_FOUND );
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

    @Transactional
    public RecruitmentResponse updateRecruitment(RecruitmentUpdateRequest request, MultipartFile image, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Band band = bandRepository.findById(request.getBandId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAND_NOT_FOUND));
        
        if(request.getStatus() != null) {
            band.setStatus(request.getStatus());
        }

        if(image != null && !image.isEmpty()) {
            String profileImageUrl = s3Uploader.upload(image, "band-profile-images");
            band.setProfileImageUrl(profileImageUrl);
        }
        if(request.getRepresentativeSong() != null) {
            Track track = trackRepository.findBySpotifyId(request.getRepresentativeSong())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.TRACK_NOT_FOUND));
            band.setRepresentativeTrack(track);
        }
        if(request.getName() != null) {
            band.setName(request.getName());
        }
        if(request.getDescription() != null) {
            band.setDescription(request.getDescription());
        }
        if(request.getEndDate() != null) {
            band.setEndDate(request.getEndDate());
        }
        if(request.getAutoClose() != null) {
            band.setAutoClose(request.getAutoClose());
        }
        if(request.getAgeStart() != null) {
            band.setAgeStart(request.getAgeStart());
        }
        if(request.getAgeEnd() != null) {
            band.setAgeEnd(request.getAgeEnd());
        }
        if(request.getGender() != null) {
            band.setGender(Gender.valueOf(request.getGender().toUpperCase()));
        }
        if(request.getRegion() != null) {
            band.setRegion(request.getRegion());
        }
        if(request.getDistrict() != null) {
            band.setDistrict(request.getDistrict());
        }
        if(request.getAverageAge() != null) {
            band.setAverageAge(request.getAverageAge());
        }
        if(request.getMaleCount() != null) {
            band.setMaleCount(request.getMaleCount());
        }
        if(request.getFemaleCount() != null) {
            band.setFemaleCount(request.getFemaleCount());
        }

        if(request.getSession() != null || request.getCurrentSessions() != null){
            List<BandSession> existingSessions = bandSessionRepository.findByBandIdAndIsDeletedFalse(request.getBandId());
            Map<String, BandSession> existingMap = existingSessions.stream()
                    .collect(Collectors.toMap(bs -> bs.getSession().getName() + "_" + bs.getSessionStatus(), Function.identity()));

            Set<String> recruitingNames = request.getSession() != null ? new HashSet<>(request.getSession()) : new HashSet<>();
            Set<String> participatingNames = request.getCurrentSessions() != null ? new HashSet<>(request.getCurrentSessions()) : new HashSet<>();

            for (BandSession bs : existingSessions) {
                String name = bs.getSession().getName();
                String status = bs.getSessionStatus();
                if ((status.equals("RECRUITING") && !recruitingNames.contains(name)) ||
                        (status.equals("PARTICIPATING") && !participatingNames.contains(name))) {
                    bs.setDeleted(true);
                    bandSessionRepository.save(bs);
                }
            }
            Set<String> existingRecruiting = existingSessions.stream()
                    .filter(bs -> bs.getSessionStatus().equals("RECRUITING"))
                    .map(bs -> bs.getSession().getName())
                    .collect(Collectors.toSet());
            Set<String> existingParticipating = existingSessions.stream()
                    .filter(bs -> bs.getSessionStatus().equals("PARTICIPATING"))
                    .map(bs -> bs.getSession().getName())
                    .collect(Collectors.toSet());

            Set<String> toAddRecruiting = new HashSet<>(recruitingNames);
            toAddRecruiting.removeAll(existingRecruiting);
            Set<String> toAddParticipating = new HashSet<>(participatingNames);
            toAddParticipating.removeAll(existingParticipating);

            List<Session> sessionEntities = sessionRepository.findByNameIn(Stream.concat(toAddRecruiting.stream(), toAddParticipating.stream()).toList());
            Map<String, Session> sessionEntityMap = sessionEntities.stream()
                    .collect(Collectors.toMap(Session::getName, Function.identity()));

            for (String name : toAddRecruiting) {
                Session s = sessionEntityMap.get(name);
                if (s != null) {
                    BandSession newBs = BandSession.builder()
                            .band(band)
                            .session(s)
                            .sessionStatus("RECRUITING")
                            .build();
                    bandSessionRepository.save(newBs);
                }
            }
            for (String name : toAddParticipating) {
                Session s = sessionEntityMap.get(name);
                if (s != null) {
                    BandSession newBs = BandSession.builder()
                            .band(band)
                            .session(s)
                            .sessionStatus("PARTICIPATING")
                            .build();
                    bandSessionRepository.save(newBs);
                }
            }
        }


        if (request.getGenres() != null) {
            Map<String,Long> genreMap = genreRepository.findGenreMapByNameIn(request.getGenres()).stream()
                    .collect(Collectors.toMap(
                            GenreRepository.GenreIdName::getName,
                            GenreRepository.GenreIdName::getId
                    ));
            List<BandGenre> existing = bandGenreRepository.findByBandId(band.getId());
            Set<Long> existingIds = existing.stream()
                    .map(bg -> bg.getGenre().getId())
                    .collect(Collectors.toSet());

            Set<Long> newIds = request.getGenres().stream()
                    .map(genreMap::get)            // 이름에 대응하는 ID 가져오기
                    .filter(Objects::nonNull)     // 매핑 안 된 이름(null)들은 걸러내고
                    .collect(Collectors.toSet());  // 바로 Set<Long> 생성

            Set<Long> toDelete = new HashSet<>(existingIds);
            toDelete.removeAll(newIds);
            if (!toDelete.isEmpty()) {
                bandGenreRepository.deleteByBandIdAndGenreIdIn(band.getId(), List.copyOf(toDelete));
            }

            Set<Long> toInsert = new HashSet<>(newIds);
            toInsert.removeAll(existingIds);
            if (!toInsert.isEmpty()) {
                List<Genre> genres = genreRepository.findByIdIn(List.copyOf(toInsert));
                List<BandGenre> inserts = genres.stream()
                        .map(g -> BandGenre.builder()
                                .band(band)
                                .genre(g)
                                .build())
                        .toList();
                bandGenreRepository.saveAll(inserts);
            }
        }
        if (request.getArtistSpotifyIds() != null) {
            // DB에 없는 아티스트는 저장
            List<Artist> ArtistAll = artistRepository.findBySpotifyIdIn(request.getArtistSpotifyIds());
            Set<String> existingArtistIds = ArtistAll.stream().map(Artist::getSpotifyId).collect(Collectors.toSet());
            List<String> missingArtistIds = request.getArtistSpotifyIds().stream()
                    .filter(id -> !existingArtistIds.contains(id))
                    .toList();
            if (!missingArtistIds.isEmpty()) {
                List<Artist> newArtists = artistService.saveArtistsBySpotifyIds(missingArtistIds);
                ArtistAll = Stream.concat(ArtistAll.stream(), newArtists.stream()).toList();
            }
            Map<String, Artist> artistMap = ArtistAll.stream()
                    .collect(Collectors.toMap(Artist::getSpotifyId, Function.identity()));

            List<BandArtist> existing = bandArtistRepository.findByBandId(band.getId());
            Set<Long> existingIds = existing.stream()
                    .map(ba -> ba.getArtist().getId())
                    .collect(Collectors.toSet());

            Set<Long> newIds = request.getArtistSpotifyIds().stream()
                    .map(artistMap::get)
                    .filter(Objects::nonNull)
                    .map(Artist::getId)
                    .collect(Collectors.toSet());

            Set<Long> toDelete = new HashSet<>(existingIds);
            toDelete.removeAll(newIds);
            if (!toDelete.isEmpty()) {
                bandArtistRepository.deleteByBandIdAndArtistIdIn(band.getId(), List.copyOf(toDelete));
            }
            Set<Long> toInsert = new HashSet<>(newIds);
            toInsert.removeAll(existingIds);
            if (!toInsert.isEmpty()) {
                List<Artist> artists = artistRepository.findByIdIn(List.copyOf(toInsert));
                List<BandArtist> inserts = artists.stream()
                        .map(a -> BandArtist.builder().band(band).artist(a).build())
                        .toList();
                bandArtistRepository.saveAll(inserts);
            }
        }
        if (request.getTrackSpotifyIds() != null) {
            // DB에 없는 트랙은 저장
            List<Track> TrackAll = trackRepository.findBySpotifyIdIn(request.getTrackSpotifyIds());
            Set<String> existingTrackIds = TrackAll.stream().map(Track::getSpotifyId).collect(Collectors.toSet());
            List<String> missingTrackIds = request.getTrackSpotifyIds().stream()
                    .filter(id -> !existingTrackIds.contains(id))
                    .toList();
            if (!missingTrackIds.isEmpty()) {
                List<Track> newTracks = trackService.saveTracksBySpotifyIds(missingTrackIds);
                TrackAll = Stream.concat(TrackAll.stream(), newTracks.stream()).toList();
            }
            Map<String, Track> trackMap = TrackAll.stream()
                    .collect(Collectors.toMap(Track::getSpotifyId, Function.identity()));

            List<BandTrack> existing = bandTrackRepository.findByBandId(band.getId());
            Set<Long> existingIds = existing.stream()
                    .map(bt -> bt.getTrack().getId())
                    .collect(Collectors.toSet());

            Set<Long> newIds = request.getTrackSpotifyIds().stream()
                    .map(trackMap::get)
                    .filter(Objects::nonNull)
                    .map(Track::getId)
                    .collect(Collectors.toSet());

            Set<Long> toDelete = new HashSet<>(existingIds);
            toDelete.removeAll(newIds);
            if (!toDelete.isEmpty()) {
                bandTrackRepository.deleteByBandIdAndTrackIdIn(band.getId(), List.copyOf(toDelete));
            }
            Set<Long> toInsert = new HashSet<>(newIds);
            toInsert.removeAll(existingIds);
            if (!toInsert.isEmpty()) {
                List<Track> tracks = trackRepository.findByIdIn(List.copyOf(toInsert));
                List<BandTrack> inserts = tracks.stream()
                        .map(t -> BandTrack.builder().band(band).track(t).build())
                        .toList();
                bandTrackRepository.saveAll(inserts);
            }
        }
        if(request.getJob() != null) {
            // 직업 저장
            List<BandJob> jobs = request.getJob().stream()
                    .map(job -> BandJob.builder()
                            .band(band)
                            .job(job)
                            .build())
                    .toList();
            bandJobRepository.deleteAllByBand(band);
            bandJobRepository.saveAll(jobs);

        }
        if (request.getSnsLinks() != null) {
            List<BandSns> existing = bandSnsRepository.findByBandId(band.getId());
            Map<String, BandSns> existingMap = existing.stream()
                    .collect(Collectors.toMap(BandSns::getPlatform, Function.identity()));

            Set<String> newKeys = request.getSnsLinks().keySet();
            Set<String> toDeleteKeys = new HashSet<>(existingMap.keySet());
            toDeleteKeys.removeAll(newKeys);
            if (!toDeleteKeys.isEmpty()) {
                bandSnsRepository.deleteByBandIdAndPlatformIn(band.getId(), List.copyOf(toDeleteKeys));
            }
            request.getSnsLinks().forEach((platform, url) -> {
                BandSns prev = existingMap.get(platform);
                if (prev == null) {
                    bandSnsRepository.save(BandSns.builder()
                            .band(band)
                            .platform(platform)
                            .snsLink(url)
                            .build());
                } else if (!prev.getSnsLink().equals(url)) {
                    prev.setSnsLink(url);
                    bandSnsRepository.save(prev);
                }
            });
        }

        return RecruitmentResponse.builder()
                .bandId(band.getId())
                .bandName(band.getName())
                .profileImageUrl(band.getProfileImageUrl())
                .type("Manager")
                .createdAt(band.getCreatedAt())
                .build();
    }

    public BasicChatRoomInfo createChatRoomForApplication(Long bandId, Long memberId, String session){


        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        BandSession bandSession = bandSessionRepository.findWithBandAndSession(bandId, "RECRUITING",session )
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAND_SESSION_NOT_RECRUITED));

        Band band = bandSession.getBand();

        ChatRoom chatRoom = ChatRoom.builder()
                .name(null)
                .imageUrl(null)
                .roomType(RoomType.BAND)
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // 참여자 추가
        chatRoomService.saveBandParticipant(chatRoom, member, band.getManager());
        BandChat bandChat = BandChat.builder()
                .passFail(PassFail.PENDING)
                .chatRoom(savedRoom)
                .band(band)
                .bandSession(bandSession)
                .build();

        bandChatRepository.save(bandChat);

        return chatRoomService.getChatRoomInfo(chatRoom.getId(), memberId);
    }

    public ApplicationListResponse buildApplicantList(Band band, Member member) {

        if (!member.equals(band.getManager())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        List<BandChat> bandChatList = bandChatRepository.findByBandAndManagerParticipant(band, member);

        List<Long> roomIdList = bandChatList.stream()
                .map(bandChat -> bandChat.getChatRoom().getId())
                .toList();

        List<ChatMessage> lastMessages = chatMessageRepository.findLastMessagePerChatRoom(roomIdList);

        Map<Long, ChatMessage> lastMessageMap = lastMessages.stream()
                .collect(Collectors.toMap(
                        msg -> msg.getChatRoom().getId(),
                        Function.identity()
                ));

        List<BandChatSummaryDto> bandChatSummaryDtos = new ArrayList<>();

        for (BandChat bandChat : bandChatList) {
            Long roomId = bandChat.getChatRoom().getId();
            ChatMessage msg = lastMessageMap.get(roomId);

            ChatRoomParticipant participant = bandChat.getChatRoom().getParticipants().stream()
                    .findFirst()
                    .orElseThrow(() -> new GeneralException(ErrorStatus.PARTICIPANT_NOT_FOUND));

            bandChatSummaryDtos.add(
                    BandChatSummaryDto.builder()
                            .roomId(roomId)
                            .nickname(participant.getMember().getNickname())
                            .imageUrl(bandChat.getChatRoom().getImageUrl())
                            .session(bandChat.getBandSession().getSession().getName())
                            .content(msg != null ? msg.getContent() : "")
                            .lastMessageAt(msg != null ? msg.getCreatedAt() : null)
                            .passFail(bandChat.getPassFail())
                            .build()
            );
        }

        return ApplicationListResponse.builder()
                .bandName(band.getName())
                .bandImage(band.getProfileImageUrl())
                .status(band.getStatus())
                .bandChatList(bandChatSummaryDtos)
                .build();
    }

    public ApplicationListResponse getApplicationList(Long bandId, Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAND_NOT_FOUND));

        return buildApplicantList(band, member);
    }

    @Transactional
    public ApplicationListResponse updateApplicant(
            Long memberId,
            ApplicantUpdateRequest request,
            Long bandId
    ) {
        List<Long> roomIds = request.getApplicantUpdate().stream()
                .map(ApplicantUpdateRequest.ApplicantUpdateDto::getRoomId)
                .toList();

        List<ChatRoom> chatRooms = chatRoomRepository.findByIdIn(roomIds);

        Map<Long, String> statusMap = request.getApplicantUpdate().stream()
                .collect(Collectors.toMap(
                        ApplicantUpdateRequest.ApplicantUpdateDto::getRoomId,
                        ApplicantUpdateRequest.ApplicantUpdateDto::getStatus
                ));

        // 합불 상태 업데이트
        chatRooms.forEach(chatRoom -> {
            String statusStr = statusMap.get(chatRoom.getId());
            if (statusStr != null) {
                chatRoom.getBandChat().setPassFail(PassFail.valueOf(statusStr));
            }
        });

        //



        return getApplicationList(bandId, memberId);
    }

    public BandInquiryResponse getRecruitment(Long memberId, Long bandId){

        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAND_NOT_FOUND));

        if(!band.getManager().getId().equals(memberId)){
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        List<BandGenre> genres = bandGenreRepository.findByBandId(bandId);
        List<String> genreNames = genres.stream()
                .map(bg -> bg.getGenre().getName())
                .toList();
        List<BandSession> sessions = bandSessionRepository.findByBandIdAndIsDeletedFalse(bandId);
        List<String> session = new ArrayList<>();
        List<String> currentSessions = new ArrayList<>();

        for (BandSession bandSession : sessions) {
            if (bandSession.getSessionStatus().equals("RECRUITING")) {
                session.add(bandSession.getSession().getName());
            } else if (bandSession.getSessionStatus().equals("PARTICIPATING")) {
                currentSessions.add(bandSession.getSession().getName());
            }
        }

        List<BandArtist> artists = bandArtistRepository.findByBandId(bandId);
        List<BandInquiryResponse.Artist> artistList = artists.stream()
                .map(ba -> BandInquiryResponse.Artist.builder()
                        .name(ba.getArtist().getName())
                        .spotifyId(ba.getArtist().getSpotifyId())
                        .ImageUrl(ba.getArtist().getImageUrl())
                        .build())
                .toList();

        List<BandTrack> tracks = bandTrackRepository.findByBandId(bandId);
        List<BandInquiryResponse.Track> trackList = tracks.stream()
                .map(bt -> BandInquiryResponse.Track.builder()
                        .title(bt.getTrack().getTitle())
                        .spotifyId(bt.getTrack().getSpotifyId())
                        .imageUrl(bt.getTrack().getImageUrl())
                        .build())
                .toList();

        List<BandJob> jobs = bandJobRepository.findJobsByBandId(bandId);
        List<String> jobList = jobs.stream()
                .map(BandJob::getJob)
                .toList();

        List<BandSns> snsLinks = bandSnsRepository.findByBandId(bandId);
        Map<String, String> snsLinkMap = snsLinks.stream()
                .collect(Collectors.toMap(BandSns::getPlatform, BandSns::getSnsLink));

        BandInquiryResponse.representativeSong repSong = Optional.ofNullable(band.getRepresentativeTrack())
                .map(track -> BandInquiryResponse.representativeSong.builder()
                        .spotifyId(track.getSpotifyId())
                        .artist(track.getArtist())
                        .trackTitle(track.getTitle())
                        .build()
                )
                .orElse(null);

        return BandInquiryResponse.builder()
                .representativeSong(repSong)
                .profileImageUrl(band.getProfileImageUrl())
                .status(band.getStatus())
                .name(band.getName())
                .description(band.getDescription())
                .endDate(band.getEndDate())
                .autoClose(band.getAutoClose())
                .ageStart(band.getAgeStart())
                .ageEnd(band.getAgeEnd())
                .gender(String.valueOf(band.getGender()))
                .region(band.getRegion())
                .averageAge(band.getAverageAge())
                .maleCount(band.getMaleCount())
                .femaleCount(band.getFemaleCount())
                .sessions(session)
                .currentSessions(currentSessions)
                .genres(genreNames)
                .artists(artistList)
                .tracks(trackList)
                .jobs(jobList)
                .snsLink(snsLinkMap)
                .build();
    }











}
