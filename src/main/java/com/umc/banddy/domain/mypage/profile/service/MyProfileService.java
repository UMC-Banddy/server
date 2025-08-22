package com.umc.banddy.domain.mypage.profile.service;

import com.umc.banddy.domain.member.domain.Genre;
import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.domain.Session;
import com.umc.banddy.domain.member.domain.mapping.MemberGenre;
import com.umc.banddy.domain.member.domain.mapping.MemberSession;
import com.umc.banddy.domain.member.enums.Gender;
import com.umc.banddy.domain.member.enums.Level;
import com.umc.banddy.domain.member.enums.SessionType;
import com.umc.banddy.domain.member.repository.*;

import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import com.umc.banddy.domain.music.artist.repository.ArtistRepository;
import com.umc.banddy.domain.music.artist.repository.MemberArtistRepository;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import com.umc.banddy.domain.music.track.repository.MemberTrackRepository;

import com.umc.banddy.domain.mypage.profile.converter.MyProfileConverter;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileResponse;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileUpdateRequest;

import com.umc.banddy.domain.member.domain.Keyword;
import com.umc.banddy.domain.member.domain.mapping.MemberKeyword;

import com.umc.banddy.global.infra.S3Uploader;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyProfileService {

    private final MemberRepository memberRepository;
    private final MemberTrackRepository memberTrackRepository;

    private final MemberKeywordRepository memberKeywordRepository;
    private final KeywordRepository keywordRepository;

    private final SessionRepository sessionRepository;
    private final MemberSessionRepository memberSessionRepository;
    private final MemberGenreRepository memberGenreRepository;

    private final MemberArtistRepository memberArtistRepository;
    private final ArtistRepository artistRepository;
    private final com.umc.banddy.domain.member.repository.GenreRepository genreRepository;

    private final JwtTokenUtil jwtTokenUtil;
    private final S3Uploader s3Uploader;

    // =========================
    // 조회
    // =========================
    public MyProfileResponse getMyProfile(HttpServletRequest request) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(JwtTokenUtil.extractToken(request));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));

        List<MemberKeyword> keywordsAfter = memberKeywordRepository.findByMemberId(memberId);
        List<MemberTrack> savedTracksAfter = memberTrackRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
        List<MyProfileResponse.SessionInfo> sessionInfosAfter =
                memberSessionRepository.findByMemberId(memberId).stream()
                        .map(ms -> {
                            String name = (ms.getSession() != null && ms.getSession().getName() != null)
                                    ? ms.getSession().getName()
                                    : (ms.getSessionType() != null ? ms.getSessionType().name() : null);
                            String level = (ms.getLevel() != null) ? ms.getLevel().name() : null;
                            return (name == null) ? null : new MyProfileResponse.SessionInfo(name, level);
                        })
                        .filter(java.util.Objects::nonNull)
                        .toList();
        List<String> interestedGenresAfter = memberGenreRepository.findByMemberId(memberId).stream()
                .map(mg -> mg.getGenre().getName())
                .toList();

        return MyProfileConverter.toMyProfileResponse(
                member,
                keywordsAfter,           // 키워드 리스트
                savedTracksAfter,
                sessionInfosAfter,
                interestedGenresAfter,
                true                     // 키워드 오버로드 호출 플래그
        );
    }

    // =========================
    // 수정
    // =========================
    @Transactional
    public MyProfileResponse updateMyProfile(
            HttpServletRequest request,
            MyProfileUpdateRequest dto,
            MultipartFile profileImage
    ) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(JwtTokenUtil.extractToken(request));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));

        // 1) 프로필 이미지
        String uploadedProfileUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            uploadedProfileUrl = s3Uploader.upload(profileImage, "profiles/" + memberId);
        }

        // 2) 멤버 기본 필드
        Member updated = Member.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .refreshToken(member.getRefreshToken())
                .nickname(nvl(dto.getNickname(), member.getNickname()))
                .age(dto.getAge() != null ? dto.getAge() : member.getAge())
                .gender(parseOrKeepGender(dto.getGender(), member.getGender()))
                .region(nvl(dto.getRegion(), member.getRegion()))
                .bio(nvl(dto.getBio(), member.getBio()))
                .profileImageUrl(uploadedProfileUrl != null ? uploadedProfileUrl : member.getProfileImageUrl())
                .mediaUrl(nvl(dto.getMediaUrl(), member.getMediaUrl()))
                .status(member.getStatus())
                .role(member.getRole())
                .inactiveDate(member.getInactiveDate())
                .build();
        memberRepository.save(updated);

        // 3) 세션 갱신 (FK + ENUM 동시 저장)
        if (dto.getAvailableSessions() != null) {
            memberSessionRepository.deleteByMemberId(memberId);

            for (MyProfileUpdateRequest.SessionInfo si : dto.getAvailableSessions()) {
                // level
                final Level level = (si.getLevel() == null || si.getLevel().isBlank())
                        ? null : Level.valueOf(si.getLevel().trim().toUpperCase());

                // sessionType (구버전 보정 포함)
                if (si.getSessionType() == null || si.getSessionType().isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sessionType이 필요합니다.");
                }
                String raw = si.getSessionType().trim().toUpperCase();
                if ("GUITAR".equals(raw)) raw = "ELECTRIC_GUITAR"; // 과거 값 보정
                final SessionType st = SessionType.valueOf(raw);

                // ✅ 세션 마스터에서 enum으로 직접 찾음
                Session sessionEntity = sessionRepository.findBySessionType(st)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "세션 마스터에 없는 타입: " + st));

                try {
                    memberSessionRepository.save(
                            MemberSession.builder()
                                    .member(member)
                                    .session(sessionEntity)  // ✅ FK 채움 (NOT NULL)
                                    .sessionType(st)         // ✅ enum도 보관 (원치 않으면 제거 가능)
                                    .level(level)
                                    .build()
                    );
                } catch (DataIntegrityViolationException e) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "중복/제약 오류: " + st);
                }
            }
        }


        // 4) 장르 갱신
        if (dto.getGenres() != null) {
            List<String> input = normalizeDistinct(dto.getGenres());
            List<String> missing = new ArrayList<>();
            Map<String, Genre> foundMap = new LinkedHashMap<>();

            for (String name : input) {
                Genre g = genreRepository.findByName(name).orElse(null);
                if (g == null) missing.add(name);
                else foundMap.put(name, g);
            }
            if (!missing.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "존재하지 않는 장르: " + String.join(", ", missing));
            }

            memberGenreRepository.deleteByMemberId(memberId);
            for (Genre g : foundMap.values()) {
                memberGenreRepository.save(MemberGenre.builder()
                        .member(member)
                        .genre(g)
                        .build());
            }
        }

        // 5) 아티스트 갱신
        if (dto.getArtists() != null) {
            List<String> input = normalizeDistinct(dto.getArtists());
            List<Artist> found = artistRepository.findByNameIgnoreCaseIn(input);
            Map<String, Artist> byLower = found.stream()
                    .collect(Collectors.toMap(a -> a.getName().trim().toLowerCase(), a -> a,
                            (a, b) -> a, LinkedHashMap::new));

            List<String> missing = input.stream()
                    .filter(n -> !byLower.containsKey(n.toLowerCase()))
                    .toList();
            if (!missing.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "존재하지 않는 아티스트: " + String.join(", ", missing));
            }

            memberArtistRepository.deleteByMemberId(memberId);
            for (String n : input) {
                Artist a = byLower.get(n.toLowerCase());
                memberArtistRepository.save(MemberArtist.builder()
                        .member(member)
                        .artist(a)
                        .build());
            }
        }

        // 6) 키워드 갱신
        if (dto.getKeywords() != null) {
            List<String> input = normalizeDistinct(dto.getKeywords());
            memberKeywordRepository.deleteByMemberId(memberId);

            // ❗Keyword는 category가 NOT NULL → 임의 생성하지 않고, 반드시 DB에 존재해야만 매핑
            List<String> missing = new ArrayList<>();

            for (String raw : input) {
                Keyword keyword = keywordRepository.findByContentIgnoreCase(raw).orElse(null);
                if (keyword == null) {
                    missing.add(raw);
                    continue;
                }

                try {
                    memberKeywordRepository.save(
                            com.umc.banddy.domain.member.domain.mapping.MemberKeyword.builder()
                                    .member(member)
                                    .keyword(keyword)
                                    .build()
                    );
                } catch (DataIntegrityViolationException e) {
                    // (member_id, keyword_id) UNIQUE 충돌
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "중복된 키워드: " + keyword.getContent());
                }
            }

            if (!missing.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "존재하지 않는 키워드: " + String.join(", ", missing));
            }
        }

        return getMyProfile(request);
    }

    // =========================
    // 헬퍼
    // =========================
    private String nvl(String v, String fallback) {
        return (v != null) ? v : fallback;
    }

    private Gender parseOrKeepGender(String raw, Gender fallback) {
        if (raw == null) return fallback;
        try {
            return Gender.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 성별 값: " + raw);
        }
    }

    private Level parseLevelOrNull(String raw) {
        if (raw == null || raw.isBlank()) return null;
        return Level.valueOf(raw.trim().toUpperCase());
    }

    private SessionType parseSessionTypeOrNull(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String v = raw.trim().toUpperCase();
        if ("GUITAR".equals(v)) v = "ELECTRIC_GUITAR"; // 구버전 보정
        return SessionType.valueOf(v);
    }

    private List<String> normalizeDistinct(List<String> arr) {
        if (arr == null) return List.of();
        return arr.stream()
                .filter(Objects::nonNull)
                .map(this::normalizeString)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .stream().toList();
    }

    private String normalizeString(String s) {
        if (s == null) return null;
        return s.trim();
    }
}
