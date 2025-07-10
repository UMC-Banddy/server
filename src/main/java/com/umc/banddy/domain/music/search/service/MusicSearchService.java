package com.umc.banddy.domain.music.search.service;

import com.umc.banddy.domain.music.search.web.dto.AlbumInfo;
import com.umc.banddy.domain.music.search.web.dto.ArtistInfo;
import com.umc.banddy.domain.music.search.web.dto.SearchAllResult;
import com.umc.banddy.domain.music.search.web.dto.TrackInfo;
import com.umc.banddy.global.security.oauth.SpotifyTokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.AlbumType;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchAlbumsRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MusicSearchService {

    private final SpotifyTokenManager tokenManager;

    @Autowired
    public MusicSearchService(SpotifyTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }


    private TrackInfo toTrackInfo(Track track) {
        String spotifyId = track.getId();
        String title = track.getName();
        String artist = track.getArtists().length > 0 ? track.getArtists()[0].getName() : "Unknown Artist";
        String album = track.getAlbum() != null ? track.getAlbum().getName() : "Unknown Album";
        String imageUrl = (track.getAlbum() != null && track.getAlbum().getImages() != null && track.getAlbum().getImages().length > 0)
                ? track.getAlbum().getImages()[0].getUrl() : null;
        String duration = null;
        if (track.getDurationMs() != null) {
            int totalSeconds = track.getDurationMs() / 1000;
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            duration = String.format("%d:%02d", minutes, seconds);
        }
        return TrackInfo.builder()
                .spotifyId(spotifyId)
                .title(title)
                .artist(artist)
                .album(album)
                .duration(duration)
                .imageUrl(imageUrl)
                .build();
    }

    private ArtistInfo toArtistInfo(Artist artist) {
        String spotifyId = artist.getId();
        String name = artist.getName();
        String genres = (artist.getGenres() != null && artist.getGenres().length > 0)
                ? String.join(", ", artist.getGenres())
                : null;
        String imageUrl = (artist.getImages() != null && artist.getImages().length > 0)
                ? artist.getImages()[0].getUrl()
                : null;
        return ArtistInfo.builder()
                .spotifyId(spotifyId)
                .name(name)
                .genres(genres)
                .imageUrl(imageUrl)
                .build();
    }

    private AlbumInfo toAlbumInfo(AlbumSimplified album) {
        String spotifyId = album.getId();
        String name = album.getName();
        String artistNames = (album.getArtists() != null && album.getArtists().length > 0)
                ? Arrays.stream(album.getArtists()).map(ArtistSimplified::getName).collect(Collectors.joining(", "))
                : "Unknown Artist";
        String releaseDate = album.getReleaseDate();
        String imageUrl = (album.getImages() != null && album.getImages().length > 0)
                ? album.getImages()[0].getUrl()
                : null;
        return AlbumInfo.builder()
                .spotifyId(spotifyId)
                .name(name)
                .artists(artistNames)
                .releaseDate(releaseDate)
                .imageUrl(imageUrl)
                .build();
    }


    public List<TrackInfo> searchTracks(String query, int limit, int offset) {
        try {
            SpotifyApi spotifyApi = tokenManager.getSpotifyApi();
            SearchTracksRequest request = spotifyApi.searchTracks(query)
                    .limit(limit)
                    .offset(offset)
                    .build();
            Paging<Track> paging = request.execute();
            return Arrays.stream(paging.getItems())
                    .map(this::toTrackInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of(TrackInfo.builder().title("에러").artist(e.getMessage()).build());
        }
    }

    public List<ArtistInfo> searchArtists(String query, int limit, int offset) {
        try {
            SpotifyApi spotifyApi = tokenManager.getSpotifyApi();
            SearchArtistsRequest request = spotifyApi.searchArtists(query)
                    .limit(limit)
                    .offset(offset)
                    .build();
            Paging<Artist> paging = request.execute();
            return Arrays.stream(paging.getItems())
                    .map(this::toArtistInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of(ArtistInfo.builder().name("에러").genres(e.getMessage()).build());
        }
    }

    public List<AlbumInfo> searchAlbums(String query, int limit, int offset) {
        try {
            SpotifyApi spotifyApi = tokenManager.getSpotifyApi();
            SearchAlbumsRequest request = spotifyApi.searchAlbums(query)
                    .limit(limit)
                    .offset(offset)
                    .build();
            Paging<AlbumSimplified> paging = request.execute();
            return Arrays.stream(paging.getItems())
                    .map(this::toAlbumInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public SearchAllResult searchAll(String query, int limit, int offset) {
        try {
            SpotifyApi spotifyApi = tokenManager.getSpotifyApi();
            var request = spotifyApi.searchItem(query, "track,artist,album")
                    .limit(limit)
                    .offset(offset)
                    .build();
            var result = request.execute();

            List<TrackInfo> tracks = result.getTracks() != null && result.getTracks().getItems() != null
                    ? Arrays.stream(result.getTracks().getItems()).map(this::toTrackInfo).collect(Collectors.toList())
                    : new ArrayList<>();

            List<ArtistInfo> artists = result.getArtists() != null && result.getArtists().getItems() != null
                    ? Arrays.stream(result.getArtists().getItems()).map(this::toArtistInfo).collect(Collectors.toList())
                    : new ArrayList<>();

            List<AlbumInfo> albums = result.getAlbums() != null && result.getAlbums().getItems() != null
                    ? Arrays.stream(result.getAlbums().getItems()).map(this::toAlbumInfo).collect(Collectors.toList())
                    : new ArrayList<>();

            return new SearchAllResult(tracks, artists, albums);

        } catch (Exception e) {
            return new SearchAllResult(
                    List.of(TrackInfo.builder().title("에러").artist(e.getMessage()).build()),
                    List.of(ArtistInfo.builder().name("에러").genres(e.getMessage()).build()),
                    List.of(AlbumInfo.builder().name("에러").artists(e.getMessage()).build())
            );
        }
    }
}

