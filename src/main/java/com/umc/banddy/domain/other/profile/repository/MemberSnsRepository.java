package com.umc.banddy.domain.other.profile.repository;

import com.umc.banddy.domain.other.profile.domain.mapping.MemberSns;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberSnsRepository extends JpaRepository<MemberSns, Long> {
    List<MemberSns> findById2(Long memberId);
}
