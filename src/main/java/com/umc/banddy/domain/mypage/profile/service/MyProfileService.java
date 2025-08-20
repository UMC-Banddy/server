package com.umc.banddy.domain.mypage.profile.service;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.domain.mapping.MemberSession;
import com.umc.banddy.domain.member.enums.Gender;
import com.umc.banddy.domain.member.enums.Level;
import com.umc.banddy.domain.member.enums.SessionType;
import com.umc.banddy.domain.member.repository.MemberGenreRepository;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import com.umc.banddy.domain.member.repository.SessionRepository;
import com.umc.banddy.domain.music.track.repository.MemberTrackRepository;
import com.umc.banddy.domain.other.profile.domain.mapping.MemberTag;
import com.umc.banddy.domain.other.profile.repository.MemberTagRepository;
import com.umc.banddy.domain.member.repository.MemberSessionRepository;
import com.umc.banddy.domain.mypage.profile.converter.MyProfileConverter;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileResponse;
import com.umc.banddy.domain.mypage.profile.web.dto.MyProfileUpdateRequest;
import com.umc.banddy.global.infra.S3Uploader;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MyProfileService {

    private final MemberRepository memberRepository;
    private final MemberTrackRepository memberTrackRepository;
    private final MemberTagRepository memberTagRepository;
    private final MemberSessionRepository memberSessionRepository;
    private final MemberGenreRepository memberGenreRepository;
    private final S3Uploader s3Uploader;
    private final SessionRepository sessionRepository;
    private final JwtTokenUtil jwtTokenUtil;

    // 내 프로필 조회
    public MyProfileResponse getMyProfile(HttpServletRequest request) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(JwtTokenUtil.extractToken(request));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<MemberTag> tags = memberTagRepository.findByMemberId(memberId);

        List<MemberTrack> savedTracks = memberTrackRepository.findByMemberIdOrderByCreatedAtDesc(memberId);

        List<MyProfileResponse.SessionInfo> sessionInfos =
                memberSessionRepository.findByMemberId(memberId).stream()
                        .map(ms -> new MyProfileResponse.SessionInfo(
                                ms.getSession().getName(),
                                ms.getLevel().toString()
                        ))
                        .toList();

        List<String> interestedGenres = memberGenreRepository.findByMemberId(memberId).stream()
                .map(mg -> mg.getGenre().getName()) // Genre 엔티티의 필드명에 맞게
                .toList();

        return MyProfileConverter.toMyProfileResponse(
                member,
                tags,
                savedTracks,
                sessionInfos,
                interestedGenres
        );
    }

    @Transactional
    public void updateMyProfile(HttpServletRequest request,
                                MyProfileUpdateRequest dto,
                                MultipartFile profileImage) {
        Long memberId = jwtTokenUtil.getMemberIdFromToken(JwtTokenUtil.extractToken(request));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        String uploadedUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            uploadedUrl = s3Uploader.upload(profileImage, "profile"); // S3 경로
        }

        Member updated = Member.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .refreshToken(member.getRefreshToken())
                .nickname(dto.getNickname() != null ? dto.getNickname() : member.getNickname())
                .age(dto.getAge() != null ? dto.getAge() : member.getAge())
                .gender(dto.getGender() != null ? Gender.valueOf(dto.getGender()) : member.getGender())
                .region(dto.getRegion() != null ? dto.getRegion() : member.getRegion())
                .bio(dto.getBio() != null ? dto.getBio() : member.getBio())
                .profileImageUrl(uploadedUrl != null ? uploadedUrl : member.getProfileImageUrl())
                .mediaUrl(dto.getMediaUrl() != null ? dto.getMediaUrl() : member.getMediaUrl())
                .status(member.getStatus())
                .role(member.getRole())
                .inactiveDate(member.getInactiveDate())
                .build();

        memberRepository.save(updated);


        // 세션 업데이트
        if (dto.getAvailableSessions() != null) {
            List<MemberSession> existingSessions = memberSessionRepository.findByMemberId(memberId);

            Map<SessionType, MemberSession> existingMap = existingSessions.stream()
                    .collect(Collectors.toMap(MemberSession::getSessionType, ms -> ms));

            Set<SessionType> newSessionTypes = new HashSet<>();


            for (MyProfileUpdateRequest.SessionInfo sessionInfo : dto.getAvailableSessions()) {
                SessionType type = SessionType.valueOf(sessionInfo.getSessionType().trim());
                Level level = Level.valueOf(sessionInfo.getLevel().trim());

                newSessionTypes.add(type);

                if (existingMap.containsKey(type)) {

                    MemberSession existing = existingMap.get(type);
                    existing.setLevel(level);
                    memberSessionRepository.save(existing);
                } else {

                    MemberSession newSession = MemberSession.builder()
                            .member(member)
                            .sessionType(type)
                            .level(level)
                            .build();
                    memberSessionRepository.save(newSession);
                }
            }


            for (SessionType oldType : existingMap.keySet()) {
                if (!newSessionTypes.contains(oldType)) {
                    memberSessionRepository.delete(existingMap.get(oldType));
                }
            }
        }
    }
}