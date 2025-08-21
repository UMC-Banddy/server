package com.umc.banddy.domain.member.repository;

import com.umc.banddy.domain.member.domain.mapping.MemberGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberGenreRepository extends JpaRepository<MemberGenre, Long> {
    List<MemberGenre> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);

    @Query("""
        select mg.genre.id
        from MemberGenre mg
        where mg.member.id = :memberId
        group by mg.genre.id
        order by count(mg.id) desc
        """)
    Optional<Long> findTopGenreIdByMemberId(Long memberId);

    @Query("""
        select mg.genre.name
        from MemberGenre mg
        where mg.member.id = :memberId
        group by mg.genre.name
        order by count(mg.id) desc
        """)
    Optional<String> findTopGenreNameByMemberId(Long memberId);
}
