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
                            // session 엔티티가 null이면 enum 이름으로 대체
                            String name = (ms.getSession() != null && ms.getSession().getName() != null)
                                    ? ms.getSession().getName()
                                    : (ms.getSessionType() != null ? ms.getSessionType().name() : null);

                            String level = (ms.getLevel() != null) ? ms.getLevel().name() : null;

                            // name이 null이면 제외
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

        // 1) 프로필 이미지 S3 업로드 → URL 반영
        String uploadedProfileUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            uploadedProfileUrl = s3Uploader.upload(profileImage, "profiles/" + memberId);
        }

        // 2) 멤버 기본 필드 업데이트 (null-safe)
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

        // 3) 세션(악기) 갱신: replace but idempotent
        if (dto.getAvailableSessions() != null) {
            List<MemberSession> existingSessions = memberSessionRepository.findByMemberId(memberId);
            Map<SessionType, MemberSession> existingMap = existingSessions.stream()
                    .collect(Collectors.toMap(MemberSession::getSessionType, ms -> ms));

            // 목표 세트
            Set<SessionType> targetTypes = new HashSet<>();
            dto.getAvailableSessions().forEach(si -> {
                SessionType type = SessionType.valueOf(si.getSessionType().trim().toUpperCase());
                Level level = Level.valueOf(si.getLevel().trim().toUpperCase());
                targetTypes.add(type);

                MemberSession exist = existingMap.get(type);
                if (exist != null) {
                    if (exist.getLevel() != level) {
                        exist.setLevel(level);
                        memberSessionRepository.save(exist);
                    }
                } else {
                    memberSessionRepository.save(MemberSession.builder()
                            .member(member)
                            .sessionType(type)
                            .level(level)
                            .build());
                }
            });
            // 제거 대상만 삭제
            for (SessionType oldType : existingMap.keySet()) {
                if (!targetTypes.contains(oldType)) {
                    memberSessionRepository.delete(existingMap.get(oldType));
                }
            }
        }

        // 4) 장르 갱신: 입력 정규화 + 검증(없는 값 400) + replace
        if (dto.getGenres() != null) {
            List<String> input = normalizeDistinct(dto.getGenres()); // trim + blank skip + distinct(원본순서 유지)

            // 존재 검증 (화이트리스트 정책)
            List<String> missing = new ArrayList<>();
            Map<String, Genre> foundMap = new LinkedHashMap<>();
            for (String name : input) {
                // 필요 시 findByNameIgnoreCase 로 변경
                Genre g = genreRepository.findByName(name).orElse(null);
                if (g == null) missing.add(name);
                else foundMap.put(name, g);
            }
            if (!missing.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "존재하지 않는 장르: " + String.join(", ", missing)
                );
            }

            // replace
            memberGenreRepository.deleteByMemberId(memberId);
            for (Genre g : foundMap.values()) {
                memberGenreRepository.save(MemberGenre.builder()
                        .member(member)
                        .genre(g)
                        .build());
            }
        }

        // 5) 아티스트 갱신: 사전등록만 허용 + replace (spotify_id NOT NULL 제약 회피)
        if (dto.getArtists() != null) {
            List<String> input = normalizeDistinct(dto.getArtists());

            // 배치 조회(없으면 개별 조회로 대체 가능)
            List<Artist> found = artistRepository.findByNameIgnoreCaseIn(input);
            Map<String, Artist> byLower = found.stream()
                    .collect(Collectors.toMap(a -> a.getName().trim().toLowerCase(), a -> a,
                            (a, b) -> a, LinkedHashMap::new));

            List<String> missing = input.stream()
                    .filter(n -> !byLower.containsKey(n.toLowerCase()))
                    .toList();

            if (!missing.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "존재하지 않는 아티스트: " + String.join(", ", missing) + " (사전 등록 필요)"
                );
            }

            // replace
            memberArtistRepository.deleteByMemberId(memberId);
            for (String n : input) {
                Artist a = byLower.get(n.toLowerCase()); // 반드시 존재
                memberArtistRepository.save(MemberArtist.builder()
                        .member(member)
                        .artist(a)
                        .build());
            }
        }

        // 6) 태그(키워드) 갱신: 정규화 + 중복제거 + 없으면 생성 허용 + 차집합 갱신(idempotent)
        if (dto.getKeywords() != null) {
            List<String> input = normalizeDistinct(dto.getKeywords());

            // 목표 Tag 엔티티 확보(없으면 생성 허용)
            Map<String, Tag> lowerToTag = new LinkedHashMap<>();
            for (String name : input) {
                String lowered = name.toLowerCase();
                Tag t = tagRepository.findByName(name).orElse(null); // 필요 시 ignoreCase 메서드로 교체
                if (t == null) {
                    t = tagRepository.save(Tag.builder().name(name).build());
                }
                lowerToTag.put(lowered, t);
            }

            // 현재 보유 관계 조회
            List<MemberTag> currentEntities = memberTagRepository.findByMemberId(memberId);
            Set<Long> current = currentEntities.stream()
                    .map(mt -> mt.getTag().getId())
                    .collect(Collectors.toSet());

            // 목표 집합
            // id → Tag 매핑도 만들어둔다(추가 시 사용)
            Map<Long, Tag> idToTag = lowerToTag.values().stream()
                    .collect(Collectors.toMap(Tag::getId, t -> t));

            Set<Long> target = new LinkedHashSet<>(idToTag.keySet());

            // 차집합
            Set<Long> toAdd = new LinkedHashSet<>(target);
            toAdd.removeAll(current);

            Set<Long> toRemove = new LinkedHashSet<>(current);
            toRemove.removeAll(target);

            // 제거: 현재 엔티티 중 tag_id가 toRemove인 것만 삭제
            if (!toRemove.isEmpty()) {
                for (MemberTag mt : currentEntities) {
                    Long tid = mt.getTag().getId();
                    if (toRemove.contains(tid)) {
                        memberTagRepository.delete(mt);
                    }
                }
            }

            // 추가: 현재에 없는 것만 안전하게 삽입 (동시성 대비 예외 무해화)
            for (Long tagId : toAdd) {
                try {
                    memberTagRepository.save(
                            MemberTag.builder()
                                    .member(member)
                                    .tag(idToTag.get(tagId))
                                    .build()
                    );
                } catch (DataIntegrityViolationException ignore) {
                    // UNIQUE(member_id, tag_id) 레이스 충돌 무해화
                }
            }
        }

        // 7) 최종 결과 반환
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

    /**
     * 문자열 리스트 정규화: null/blank 제거 + trim + 입력 중복 제거(원래 순서 유지)
     */
    private List<String> normalizeDistinct(List<String> arr) {
        if (arr == null) return List.of();
        return arr.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new)) // 순서 유지 + 중복 제거
                .stream().toList();
    }
}
