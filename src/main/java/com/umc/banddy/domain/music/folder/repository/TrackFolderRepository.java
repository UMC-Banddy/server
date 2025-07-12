package com.umc.banddy.domain.music.folder.repository;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.music.folder.domain.TrackFolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackFolderRepository extends JpaRepository<TrackFolder, Long> {
    List<TrackFolder> findAllByMember(Member member);
}
