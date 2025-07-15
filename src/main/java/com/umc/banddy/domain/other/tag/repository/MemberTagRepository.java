package com.umc.banddy.domain.other.tag.repository;

import com.umc.banddy.domain.other.tag.domain.mapping.MemberTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTagRepository extends JpaRepository<MemberTag, Long> {
    List<MemberTag> findByMemberId(Long memberId);
}
