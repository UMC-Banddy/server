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
import com.umc.banddy.domain.music.track.domain.Track;
import com.umc.banddy.domain.music.track.repository.TrackRepository;
import com.umc.banddy.global.infra.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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


    @Transactional
    public RecruitmentResponse createRecruitment(RecruitmentRequest request, MultipartFile image, Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다. ID: " + memberId));

//        String profileImageUrl = (request.getImage() != null && !request.getImage().isEmpty())
//                ? s3Uploader.upload(request.getImage(), "band-profile-images") : null;

        String profileImageUrl = (image != null && !image.isEmpty())
                ? s3Uploader.upload(image, "band-profile-images") : null;

        Band band = Band.builder()
                .status(BandStatus.RECRUITING)
                .profileImageUrl(profileImageUrl)
                .representativeSong(request.getRepresentativeSong())
                .name(request.getName())
                .description(request.getDescription())
                .endDate(request.getEndDate())
                .autoClose(request.getAutoClose())
                .ageStart(request.getAgeStart())
                .ageEnd(request.getAgeEnd())
                .gender(Gender.valueOf(request.getGender().toUpperCase()))
                .region(request.getRegion())
                .district(request.getDistrict())
                .status(BandStatus.RECRUITING)
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
        List<Genre> GenreAll = genreRepository.findByNameIn(request.getGenres());
        Map<String, Genre> genreMap = GenreAll.stream()
                .collect(Collectors.toMap(Genre::getName, Function.identity()));

        List<BandGenre> genres = request.getGenres().stream()
                .map(genreName -> {
                    Genre g = genreMap.get(genreName);
                    if (g == null) throw new IllegalArgumentException("장르이 존재하지 않습니다: " + genreName);
                return BandGenre.builder()
                            .band(savedBand)
                            .genre(g)
                            .build();
                }).toList();


        // 밴드 아티스트 저장
        List<Artist> ArtistAll = artistRepository.findBySpotifyIdIn(request.getArtistSpotifyIds());
        Map<String, Artist> artistMap = ArtistAll.stream()
                .collect(Collectors.toMap(Artist::getSpotifyId, Function.identity()));

        List<BandArtist> artists = request.getArtistSpotifyIds().stream()
                .map(artistId -> {
                    Artist a = artistMap.get(artistId);
                    if (a == null) throw new IllegalArgumentException("아티스트가 존재하지 않습니다: " + artistId );
                    return BandArtist.builder()
                            .band(savedBand)
                            .artist(a)
                            .build();
                }).toList();

        // 밴드 트랙 저장
        List<Track> TrackAll = trackRepository.findBySpotifyIdIn(request.getTrackSpotifyIds());
        Map<String, Track> trackMap = TrackAll.stream()
                .collect(Collectors.toMap(Track::getSpotifyId, Function.identity()));

        List<BandTrack> tracks = request.getTrackSpotifyIds().stream()
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

    @Transactional
    public RecruitmentResponse updateRecruitment(RecruitmentUpdateRequest request, MultipartFile image, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다. ID: " + memberId));

        Band band = bandRepository.findById(request.getBandId())
                .orElseThrow(() -> new IllegalArgumentException("해당 밴드가 존재하지 않습니다."));

//        if(Objects.equals(member.getId(), band.getManager().getId())) throw new IllegalArgumentException("수정할 수 없는 사용자 입니다");

        if(request.getStatus() != null) {
            band.setStatus(request.getStatus());
        }

//        if(request.getImage() != null && !request.getImage().isEmpty()) {
//            String profileImageUrl = s3Uploader.upload(request.getImage(), "band-profile-images");
//            band.setProfileImageUrl(profileImageUrl);
//        }
        if(image != null && !image.isEmpty()) {
            String profileImageUrl = s3Uploader.upload(image, "band-profile-images");
            band.setProfileImageUrl(profileImageUrl);
        }
        if(request.getRepresentativeSong() != null) {
            Track track = trackRepository.findBySpotifyId(request.getRepresentativeSong())
                    .orElseThrow(() -> new IllegalArgumentException("곡이 존재하지 않습니다."));
            band.setRepresentativeSong(track.getTitle());
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

        if(request.getSession() != null) {
            bandSessionRepository.deleteAllByBand(band);

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
            Map<String,Long> artistMap = artistRepository.findArtistMapBySpotifyIdIn(request.getArtistSpotifyIds()).stream()
                    .collect(Collectors.toMap(
                            ArtistRepository.ArtistIdSpotifyId::getSpotifyId,
                            ArtistRepository.ArtistIdSpotifyId::getId
                    ));
            List<BandArtist> existing = bandArtistRepository.findByBandId(band.getId());
            Set<Long> existingIds = existing.stream()
                    .map(ba -> ba.getArtist().getId())
                    .collect(Collectors.toSet());

            Set<Long> newIds = request.getArtistSpotifyIds().stream()
                    .map(artistMap::get)
                    .filter(Objects::nonNull)
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
            Map<String,Long> trackMap = trackRepository.findTrackMapBySpotifyIdIn(request.getTrackSpotifyIds()).stream()
                    .collect(Collectors.toMap(
                            TrackRepository.TrackIdSpotifyId::getSpotifyId,
                            TrackRepository.TrackIdSpotifyId::getId
                    ));
            List<BandTrack> existing = bandTrackRepository.findByBandId(band.getId());
            Set<Long> existingIds = existing.stream()
                    .map(bt -> bt.getTrack().getId())
                    .collect(Collectors.toSet());

            Set<Long> newIds = request.getTrackSpotifyIds().stream()
                    .map(trackMap::get)            // 각 Spotify ID에 매핑된 Track ID
                    .filter(Objects::nonNull)      // 매핑 안 된(없는) ID는 필터링
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

        Session sessionEntity = sessionRepository.findByName(session)
                .orElseThrow(() -> new IllegalArgumentException("세션 정보가 존재하지 않습니다: " + session));

        Band band = bandRepository.findById(bandId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 밴드입니다. ID: " + bandId));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다. ID: " + memberId));

        BandSession bandSession = bandSessionRepository.findByBandIdAndSessionStatusAndSession(bandId, "RECRUITING",sessionEntity )
                .orElseThrow(() -> new IllegalArgumentException("모집 중이지 않습니다"));

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

    public ApplicationListResponse getApplicationList(Long bandId, Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 밴드입니다."));

        if (!member.equals(band.getManager())) {throw new IllegalArgumentException("조회권한이 없습니다"+ member.getId() + member.getNickname());}

        List<BandChat> bandChatList = bandChatRepository.findByBandAndManagerParticipant(band, member);

        List<Long> roomIdList = bandChatList.stream()
                .map(bandChat -> bandChat.getChatRoom().getId())
                .toList();
        List<String> sessions = bandSessionRepository.findSessionNamesByBandIdAndStatus(bandId,"RECRUITING");

        List<ChatMessage> lastMessages = chatMessageRepository.findLastMessagePerChatRoom(roomIdList);

        Map<Long, ChatMessage> lastMessageMap = lastMessages.stream()
                .collect(Collectors.toMap(
                        msg -> msg.getChatRoom().getId(),
                        Function.identity()
                ));

        List<BandChatSummaryDto> bandChatSummaryDtos = new ArrayList<>();

        for (BandChat bandChat : bandChatList) {
            Long roomId = bandChat.getChatRoom().getId();
            ChatMessage msg = lastMessageMap.get(bandChat.getChatRoom().getId());

            ChatRoomParticipant participant = bandChat.getChatRoom().getParticipants().stream()
                    .filter(p -> p.getRole() != Role.BANDMANAGER)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("참여자가 존재하지 않습니다."));


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
                .sessions(sessions)
                .status(band.getStatus())
                .bandChatList(bandChatSummaryDtos)
                .build();
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

        chatRooms.forEach(chatRoom -> {
            String statusStr = statusMap.get(chatRoom.getId());
            if (statusStr != null) {
                chatRoom.getBandChat()
                        .setPassFail(PassFail.valueOf(statusStr));
            }
        });

        return getApplicationList(bandId, memberId);
    }

}
