package com.umc.banddy.domain.member.listener;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.enums.Status;
import jakarta.persistence.PostLoad;

public class MemberEntityListener {

    @PostLoad
    public void onPostLoad(Member member) {
        if (member.getStatus() == Status.INACTIVE) {
            throw new IllegalStateException("탈퇴한 회원입니다. 접근 불가.");
        }
    }
}
