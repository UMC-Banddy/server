package com.umc.banddy.domain.mypage.profile.service;

import com.umc.banddy.domain.member.domain.Genre;
import com.umc.banddy.domain.member.domain.Member;
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
import com.umc.banddy.domain.other.profile.domain.Tag;
import com.umc.banddy.domain.other.profile.domain.mapping.MemberTag;
import com.umc.banddy.domain.other.profile.repository.MemberTagRepository;
import com.umc.banddy.domain.mypage.profile.converter.MyProfileConverter;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileResponse;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileUpdateRequest;
import com.umc.banddy.domain.other.profile.repository.TagRepository;
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
    private final MemberTagRepository memberTagRepository;
    private final MemberSessionRepository memberSessionRepository;
    private final MemberGenreRepository memberGenreRepository;
    private final SessionRepository sessionRepository;
    private final JwtTokenUtil jwtTokenUtil;

    private final MemberArtistRepository memberArtistRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final TagRepository tagRepository;

    private final S3Uploader s3Uploader;

    // =========================
    // 조회
    // =========================
    public MyProfileResponse getMyProfile(HttpServletRequest request) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(JwtTokenUtil.extractToken(request));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));

        List<MemberTag> tags = memberTagRepository.findByMemberId(memberId);
        List<MemberTrack> savedTracks = memberTrackRepository.findByMemberIdOrderByCreatedAtDesc(memberId);

        List<MyProfileResponse.SessionInfo> sessionInfos =
                memberSessionRepository.findByMemberId(memberId).stream()
                        .map(ms -> {
                            String name = (ms.getSession() != null && ms.getSession().getName() != null)
                                    ? ms.getSession().getName()
                                    : (ms.getSessionType() != null ? ms.getSessionType().name() : null);

                            String level = (ms.getLevel() != null) ? ms.getLevel().name() : null;
                            return (name == null) ? null : new MyProfileResponse.SessionInfo(name, level);
                        })
                        .filter(Objects::nonNull)
                        .toList();

        List<String> interestedGenres = memberGenreRepository.findByMemberId(memberId).stream()
                .map(mg -> mg.getGenre().getName())
                .toList();

        return MyProfileConverter.toMyProfileResponse(
                member, tags, savedTracks, sessionInfos, interestedGenres
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

        // 3) 세션 갱신
        if (dto.getAvailableSessions() != null) {
            memberSessionRepository.deleteByMemberId(memberId);

            for (MyProfileUpdateRequest.SessionInfo si : dto.getAvailableSessions()) {
                SessionType type = SessionType.valueOf(si.getSessionType().trim().toUpperCase());
                Level level = Level.valueOf(si.getLevel().trim().toUpperCase());

                try {
                    memberSessionRepository.save(MemberSession.builder()
                            .member(member)
                            .sessionType(type)
                            .level(level)
                            .build());
                } catch (DataIntegrityViolationException e) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "중복된 세션: " + si.getSessionType());
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

        // 6) 키워드 갱신 (Tag)
        if (dto.getKeywords() != null) {
            List<String> input = normalizeDistinct(dto.getKeywords());
            memberTagRepository.deleteByMemberId(memberId);

            for (String name : input) {
                Tag tag = tagRepository.findByNameIgnoreCase(name).orElse(null);
                if (tag == null) {
                    // saveAndFlush 로 id 강제 생성
                    tag = tagRepository.saveAndFlush(Tag.builder().name(name).build());
                }

                if (tag.getId() == null) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Tag ID 생성 실패");
                }

                try {
                    memberTagRepository.save(MemberTag.builder()
                            .member(member)
                            .tag(tag)
                            .build());
                } catch (DataIntegrityViolationException e) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "중복된 키워드: " + name);
                }
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

    private List<String> normalizeDistinct(List<String> arr) {
        if (arr == null) return List.of();
        return arr.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .stream().toList();
    }
}
