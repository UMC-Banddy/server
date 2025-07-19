package com.umc.banddy.domain.chat.repository;


import com.umc.banddy.domain.chat.domain.Recruitment.Band;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface BandRepository extends JpaRepository<Band, Long> {
}
