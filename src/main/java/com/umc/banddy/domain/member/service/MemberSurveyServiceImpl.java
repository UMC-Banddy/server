package com.umc.banddy.domain.member.service;

import com.umc.banddy.domain.member.domain.*;
import com.umc.banddy.domain.member.domain.mapping.*;
import com.umc.banddy.domain.member.repository.*;
import com.umc.banddy.domain.member.web.dto.SimpleKeywordDto;
import com.umc.banddy.domain.member.web.dto.SimpleSessionDto;
import com.umc.banddy.domain.member.enums.KeywordCategory;
import com.umc.banddy.domain.member.web.dto.KeywordRequestGroup;
import com.umc.banddy.domain.member.web.dto.MemberSurveyRequest;
import com.umc.banddy.domain.member.web.dto.MemberSurveyRequest.SessionRequest;
import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import com.umc.banddy.domain.music.artist.repository.ArtistRepository;
import com.umc.banddy.domain.music.artist.repository.MemberArtistRepository;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.umc.banddy.global.infra.S3Uploader;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberSurveyServiceImpl implements MemberSurveyService {

    private final MemberRepository memberRepository;
    private final GenreRepository genreRepository;
    private final KeywordRepository keywordRepository;
    private final SessionRepository sessionRepository;
    private final SnsLinkRepository snsLinkRepository;
    private final MemberGenreRepository memberGenreRepository;
    private final MemberKeywordRepository memberKeywordRepository;
    private final MemberSessionRepository memberSessionRepository;
    private final MemberArtistRepository memberArtistRepository;
    private final ArtistRepository artistRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final S3Uploader s3Uploader;

    @Override
    @Transactional
    public void saveSurveyInfo(String accessToken, MemberSurveyRequest request,
                               MultipartFile profileImage, MultipartFile mediaFile) {

        String email = jwtTokenUtil.getEmailFromToken(accessToken);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // S3 업로드
        String profileImageUrl = (profileImage != null && !profileImage.isEmpty())
                ? s3Uploader.upload(profileImage, "profile-images") : null;

        String mediaUrl = (mediaFile != null && !mediaFile.isEmpty())
                ? s3Uploader.upload(mediaFile, "media-files") : null;

        // 프로필 업데이트
        member.updateProfile(profileImageUrl, request.getBio(), mediaUrl);

        // 장르 저장
        if (request.getGenreNames() != null) {
            request.getGenreNames().forEach(name ->
                    genreRepository.findByName(name).ifPresentOrElse(
                            genre -> memberGenreRepository.save(MemberGenre.builder()
                                    .member(member)
                                    .genre(genre)
                                    .build()),
                            () -> { throw new IllegalArgumentException("존재하지 않는 장르: " + name); }
                    )
            );
        }

        // 키워드 저장
        KeywordRequestGroup keywords = request.getKeywords();
        if (keywords != null) {
            if (keywords.getMANNER() != null) {
                for (String content : keywords.getMANNER()) {
                    keywordRepository.findByContentAndCategory(content, KeywordCategory.MANNER)
                            .ifPresent(keyword -> memberKeywordRepository.save(
                                    MemberKeyword.builder().member(member).keyword(keyword).build()));
                }
            }

            if (keywords.getSTYLE() != null) {
                for (String content : keywords.getSTYLE()) {
                    keywordRepository.findByContentAndCategory(content, KeywordCategory.STYLE)
                            .ifPresent(keyword -> memberKeywordRepository.save(
                                    MemberKeyword.builder().member(member).keyword(keyword).build()));
                }
            }

            if (keywords.getSKILL() != null) {
                for (String content : keywords.getSKILL()) {
                    keywordRepository.findByContentAndCategory(content, KeywordCategory.SKILL)
                            .ifPresent(keyword -> memberKeywordRepository.save(
                                    MemberKeyword.builder().member(member).keyword(keyword).build()));
                }
            }

            if (keywords.getFREQ() != null) {
                for (String content : keywords.getFREQ()) {
                    keywordRepository.findByContentAndCategory(content, KeywordCategory.FREQ)
                            .ifPresent(keyword -> memberKeywordRepository.save(
                                    MemberKeyword.builder().member(member).keyword(keyword).build()));
                }
            }
        }

        // 세션 저장
        if (request.getSessions() != null) {
            for (SessionRequest sessionReq : request.getSessions()) {
                sessionRepository.findByName(sessionReq.getSessionName()).ifPresent(session ->
                        memberSessionRepository.save(MemberSession.builder()
                                .member(member)
                                .session(session)
                                .level(sessionReq.getLevel())
                                .build()));
            }
        }

        // 아티스트 저장
        if (request.getArtistIds() != null) {
            request.getArtistIds().forEach(id ->
                    artistRepository.findById(id).ifPresent(artist ->
                            memberArtistRepository.save(MemberArtist.builder()
                                    .member(member)
                                    .artist(artist)
                                    .build())));
        }

        // SNS 링크 저장
        if (request.getSnsLinks() != null) {
            request.getSnsLinks().forEach(link ->
                    snsLinkRepository.save(SnsLink.builder()
                            .member(member)
                            .url(link.getUrl())
                            .platform(link.getPlatform())
                            .build()));
        }
    }


    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }
    @Override
    public List<Genre> searchGenres(String keyword) {
        return genreRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Artist> searchArtists(String keyword) {
        return artistRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public Map<KeywordCategory, List<SimpleKeywordDto>> getGroupedKeywords() {
        return keywordRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Keyword::getCategory,
                        Collectors.mapping(k -> new SimpleKeywordDto(k.getId(), k.getContent()), Collectors.toList())
                ));
    }

    @Override
    public List<SimpleSessionDto> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(s -> new SimpleSessionDto(s.getId(), s.getName()))
                .toList();
    }
}
