package com.umc.banddy.domain.mypage.profile.service;

import com.umc.banddy.domain.mypage.profile.domain.Member;
import com.umc.banddy.domain.mypage.profile.repository.MemberRepository;
import com.umc.banddy.global.infra.S3Uploader;
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

    public String uploadProfileMedia(MultipartFile file, Long memberId) {
        String uploadUrl = s3Uploader.upload(file, "profile");

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        member.updateProfileImage(uploadUrl);
        memberRepository.save(member);

        return uploadUrl;
    }
}

