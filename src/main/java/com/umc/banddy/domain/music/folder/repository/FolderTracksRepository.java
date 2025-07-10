package com.umc.banddy.domain.music.folder.repository;

import com.umc.banddy.domain.music.folder.domain.FolderTracks;
import com.umc.banddy.domain.music.folder.domain.TrackFolder;
import com.umc.banddy.domain.music.track.domain.Track;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderTracksRepository extends JpaRepository<FolderTracks, Long> {
    List<FolderTracks> findAllByTrackFolder(TrackFolder trackFolder);
    Optional<FolderTracks> findByTrackFolderAndMemberTrack(TrackFolder trackFolder, MemberTrack memberTrack);
    void deleteByTrackFolderAndTrack(TrackFolder trackFolder, Track track);
    Optional<FolderTracks> findByTrackFolderAndTrack(TrackFolder trackFolder, Track track);
    //void deleteByTrackFolderAndMemberTrack(TrackFolder trackFolder, MemberTrack memberTrack);
}

