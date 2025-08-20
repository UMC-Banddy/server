package com.umc.banddy.domain.mypage.profile.service;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.repository.MemberRepository;
import com.umc.banddy.global.infra.S3Uploader;
import com.umc.banddy.global.security.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Profile("s3")
@Service
@RequiredArgsConstructor
public class MyProfileMediaService {

    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;
    private final JwtTokenUtil jwtTokenUtil;

    @Transactional
    public String uploadProfileMedia(MultipartFile file, HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 인증
        String token = JwtTokenUtil.extractToken(request);
        Long memberId = jwtTokenUtil.getMemberIdFromToken(token);

        // 업로드
        String uploadUrl = s3Uploader.upload(file, "profile");

        // 회원 정보 업데이트
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // Member 엔티티에 아래 메서드가 있어야 함 (아래 예시 참조)
        member.updateProfile(uploadUrl, null, null);

        // save()는 선택(영속 상태면 변경감지로 flush됨)
        // memberRepository.save(member);

        return uploadUrl;
    }
}
