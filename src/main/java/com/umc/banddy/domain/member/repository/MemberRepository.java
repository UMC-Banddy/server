package com.umc.banddy.domain.member.repository;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.member.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    // 닉네임 중복 확인
    boolean existsByNickname(String nickname);

    @Query("SELECT m.email FROM Member m WHERE m.id = :receiverId")
    String findEmailById(Long receiverId); // 개인 메세지 보낼 때 사용
    List<Member> findByStatusAndInactiveDateBefore(Status status, LocalDate date);


}
