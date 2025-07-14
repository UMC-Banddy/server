package com.umc.banddy.domain.music.folder.domain;

import com.umc.banddy.domain.music.artist.domain.MemberArtist;
import com.umc.banddy.domain.music.folder.domain.ArtistFolder;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderArtists {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_folder_id")
    private ArtistFolder artistFolder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_artist_id")
    private MemberArtist memberArtist;
}

