package com.umc.banddy.domain.music.folder.repository;

import com.umc.banddy.domain.member.domain.Member;
import com.umc.banddy.domain.music.folder.domain.AlbumFolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumFolderRepository extends JpaRepository<AlbumFolder, Long> {
    List<AlbumFolder> findAllByMember(Member member);
}