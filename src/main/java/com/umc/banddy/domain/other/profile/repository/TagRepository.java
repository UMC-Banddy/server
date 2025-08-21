package com.umc.banddy.domain.other.profile.repository;

import com.umc.banddy.domain.other.profile.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    Optional<Tag> findByNameIgnoreCase(String name);

    // 배치 조회
    @Query("select t from Tag t where lower(t.name) in :names")
    List<Tag> findByLowerNames(@Param("names") Collection<String> lowerNames);
}

