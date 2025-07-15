package com.umc.banddy.domain.music.folder.repository;

import com.umc.banddy.domain.music.album.domain.Album;
import com.umc.banddy.domain.music.folder.domain.AlbumFolder;
import com.umc.banddy.domain.music.folder.domain.FolderAlbums;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderAlbumsRepository extends JpaRepository<FolderAlbums, Long> {
    List<FolderAlbums> findAllByAlbumFolder(AlbumFolder albumFolder);
    Optional<FolderAlbums> findByAlbumFolderAndAlbum(AlbumFolder albumFolder, Album album);
    void deleteByAlbumFolderAndAlbum(AlbumFolder albumFolder, Album album);
    List<FolderAlbums> findAllByAlbum(Album album);
}
