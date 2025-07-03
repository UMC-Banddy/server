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

    public List<TrackInfo> searchTracks(String query, int limit, int offset) {
        try {
            SpotifyApi spotifyApi = tokenManager.getSpotifyApi();
            SearchTracksRequest request = spotifyApi.searchTracks(query)
                    .limit(limit)
                    .offset(offset)
                    .build();
            Paging<Track> paging = request.execute();
            List<TrackInfo> results = new ArrayList<>();
            for (Track track : paging.getItems()) {
                String title = track.getName();
                String artist = track.getArtists().length > 0 ? track.getArtists()[0].getName() : "Unknown Artist";
                String album = track.getAlbum() != null ? track.getAlbum().getName() : "Unknown Album";
                String imageUrl = (track.getAlbum() != null && track.getAlbum().getImages() != null && track.getAlbum().getImages().length > 0)
                        ? track.getAlbum().getImages()[0].getUrl() : null;
                // duration_ms를 mm:ss로 변환
                String duration = null;
                if (track.getDurationMs() != null) {
                    int totalSeconds = track.getDurationMs() / 1000;
                    int minutes = totalSeconds / 60;
                    int seconds = totalSeconds % 60;
                    duration = String.format("%d:%02d", minutes, seconds);
                }

                results.add(TrackInfo.builder()
                        .title(title)
                        .artist(artist)
                        .album(album)
                        .duration(duration)
                        .imageUrl(imageUrl)
                        .build());
            }
            return results;
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
            List<ArtistInfo> results = new ArrayList<>();
            for (Artist artist : paging.getItems()) {
                String name = artist.getName();
                // genres 배열을 쉼표로 join
                String genres = (artist.getGenres() != null && artist.getGenres().length > 0)
                        ? String.join(", ", artist.getGenres())
                        : null;
                // 이미지
                String imageUrl = (artist.getImages() != null && artist.getImages().length > 0)
                        ? artist.getImages()[0].getUrl()
                        : null;

                results.add(ArtistInfo.builder()
                        .name(name)
                        .genres(genres)
                        .imageUrl(imageUrl)
                        .build());
            }
            return results;
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
            List<AlbumInfo> results = new ArrayList<>();
            for (AlbumSimplified album : paging.getItems()) {
                String name = album.getName();
                String artistNames = (album.getArtists() != null && album.getArtists().length > 0)
                        ? Arrays.stream(album.getArtists()).map(ArtistSimplified::getName).collect(Collectors.joining(", "))
                        : "Unknown Artist";
                String albumType = album.getAlbumType() != null ? album.getAlbumType().getType() : null;
                String releaseDate = album.getReleaseDate();
                String imageUrl = (album.getImages() != null && album.getImages().length > 0)
                        ? album.getImages()[0].getUrl()
                        : null;

                results.add(AlbumInfo.builder()
                        .name(name)
                        .artists(artistNames)
                        .albumType(albumType)
                        .releaseDate(releaseDate)
                        .imageUrl(imageUrl)
                        .build());
            }
            return results;
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

            // 트랙
            List<TrackInfo> tracks = new ArrayList<>();
            if (result.getTracks() != null && result.getTracks().getItems() != null) {
                for (Track track : result.getTracks().getItems()) {
                    String title = track.getName();
                    String artist = track.getArtists().length > 0 ? track.getArtists()[0].getName() : "Unknown Artist";
                    String album = (track.getAlbum() != null) ? track.getAlbum().getName() : "Unknown Album";
                    String imageUrl = (track.getAlbum() != null && track.getAlbum().getImages() != null && track.getAlbum().getImages().length > 0)
                            ? track.getAlbum().getImages()[0].getUrl() : null;
                    String duration = null;
                    if (track.getDurationMs() != null) {
                        int totalSeconds = track.getDurationMs() / 1000;
                        int minutes = totalSeconds / 60;
                        int seconds = totalSeconds % 60;
                        duration = String.format("%d:%02d", minutes, seconds);
                    }
                    tracks.add(TrackInfo.builder()
                            .title(title)
                            .artist(artist)
                            .album(album)
                            .duration(duration)
                            .imageUrl(imageUrl)
                            .build());
                }
            }

            // 아티스트
            List<ArtistInfo> artists = new ArrayList<>();
            if (result.getArtists() != null && result.getArtists().getItems() != null) {
                for (Artist artist : result.getArtists().getItems()) {
                    String name = artist.getName();
                    String genres = (artist.getGenres() != null && artist.getGenres().length > 0)
                            ? String.join(", ", artist.getGenres())
                            : null;
                    String imageUrl = (artist.getImages() != null && artist.getImages().length > 0)
                            ? artist.getImages()[0].getUrl()
                            : null;
                    artists.add(ArtistInfo.builder()
                            .name(name)
                            .genres(genres)
                            .imageUrl(imageUrl)
                            .build());
                }
            }

            // 앨범
            List<AlbumInfo> albums = new ArrayList<>();
            if (result.getAlbums() != null && result.getAlbums().getItems() != null) {
                for (AlbumSimplified album : result.getAlbums().getItems()) {
                    String name = album.getName();
                    String artistNames = (album.getArtists() != null && album.getArtists().length > 0)
                            ? Arrays.stream(album.getArtists()).map(ArtistSimplified::getName).collect(Collectors.joining(", "))
                            : "Unknown Artist";
                    String albumType = album.getAlbumType() != null ? album.getAlbumType().getType() : null;
                    String releaseDate = album.getReleaseDate();
                    String imageUrl = (album.getImages() != null && album.getImages().length > 0)
                            ? album.getImages()[0].getUrl()
                            : null;

                    albums.add(AlbumInfo.builder()
                            .name(name)
                            .artists(artistNames)
                            .albumType(albumType)
                            .releaseDate(releaseDate)
                            .imageUrl(imageUrl)
                            .build());
                }
            }

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
