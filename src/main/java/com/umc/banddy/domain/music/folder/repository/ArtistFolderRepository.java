package com.umc.banddy.domain.music.folder.repository;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.music.folder.domain.ArtistFolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistFolderRepository extends JpaRepository<ArtistFolder, Long> {
    List<ArtistFolder> findAllByMember(Member member);
}
