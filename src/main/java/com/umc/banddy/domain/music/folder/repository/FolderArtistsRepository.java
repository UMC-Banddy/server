package com.umc.banddy.domain.music.folder.repository;

import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import com.umc.banddy.domain.music.folder.domain.ArtistFolder;
import com.umc.banddy.domain.music.folder.domain.FolderArtists;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderArtistsRepository extends JpaRepository<FolderArtists, Long> {
    List<FolderArtists> findAllByArtistFolder(ArtistFolder artistFolder);
    Optional<FolderArtists> findByArtistFolderAndMemberArtist(ArtistFolder artistFolder, MemberArtist memberArtist);
    void deleteByArtistFolderAndMemberArtist(ArtistFolder artistFolder, MemberArtist memberArtist);
    List<FolderArtists> findAllByMemberArtist(MemberArtist memberArtist);
}
