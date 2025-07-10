package com.umc.banddy.domain.music.folder.converter;

import com.umc.banddy.domain.music.folder.domain.FolderTracks;
import com.umc.banddy.domain.music.folder.domain.TrackFolder;
import com.umc.banddy.domain.music.track.domain.Track;
import com.umc.banddy.domain.music.track.domain.mapping.MemberTrack;

public class FolderTracksConverter {
/*    // Request DTO → Entity
    public static FolderTracks toFolderTracks(TrackFolder folder, Track track) {
        return FolderTracks.builder()
                .trackFolder(folder)
                .track(track)
                .build();
    }*/

    public static FolderTracks toFolderTracks(TrackFolder folder, MemberTrack memberTrack) {
        return FolderTracks.builder()
                .trackFolder(folder)
                .memberTrack(memberTrack)
                .build();
    }
}
