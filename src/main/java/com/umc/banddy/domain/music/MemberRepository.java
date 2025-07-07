package com.umc.banddy.domain.music;

import com.umc.banddy.domain.music.Member;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 필요에 따라 추가적인 쿼리 메서드 정의 가능
}

