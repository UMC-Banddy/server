package com.umc.banddy.domain.music.search.service;

import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.global.security.oauth.SpotifyTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchAlbumsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutocompleteService {

    private final SpotifyTokenManager tokenManager;


    public List<String> autocompleteTracks(String query, int limit) {
        try {
            SpotifyApi spotifyApi = tokenManager.getSpotifyApi();
            SearchTracksRequest request = spotifyApi.searchTracks(query)
                    .limit(limit)
                    .build();
            Paging<Track> paging = request.execute();
            return Arrays.stream(paging.getItems())
                    .map(Track::getName)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<String> autocompleteArtists(String query, int limit) {
        try {
            SpotifyApi spotifyApi = tokenManager.getSpotifyApi();
            SearchArtistsRequest request = spotifyApi.searchArtists(query)
                    .limit(limit)
                    .build();
            se.michaelthelin.spotify.model_objects.specification.Paging<se.michaelthelin.spotify.model_objects.specification.Artist> paging = request.execute();
            return Arrays.stream(paging.getItems())
                    .map(se.michaelthelin.spotify.model_objects.specification.Artist::getName)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }


    public List<String> autocompleteAlbums(String query, int limit) {
        try {
            SpotifyApi spotifyApi = tokenManager.getSpotifyApi();
            SearchAlbumsRequest request = spotifyApi.searchAlbums(query)
                    .limit(limit)
                    .build();
            Paging<AlbumSimplified> paging = request.execute();
            return Arrays.stream(paging.getItems())
                    .map(AlbumSimplified::getName)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    // 통합 자동완성 (곡+아티스트+앨범)
    public List<String> autocompleteMusic(String query, int limit) {
        try {
            SpotifyApi spotifyApi = tokenManager.getSpotifyApi();
            var request = spotifyApi.searchItem(query, "track,artist,album")
                    .limit(limit)
                    .build();
            var result = request.execute();

            List<String> tracks = result.getTracks() != null && result.getTracks().getItems() != null
                    ? Arrays.stream(result.getTracks().getItems()).map(Track::getName).toList()
                    : List.of();
            List<String> artists = result.getArtists() != null && result.getArtists().getItems() != null
                    ? Arrays.stream(result.getArtists().getItems())
                    .map(se.michaelthelin.spotify.model_objects.specification.Artist::getName)
                    .toList()
                    : List.of();
            List<String> albums = result.getAlbums() != null && result.getAlbums().getItems() != null
                    ? Arrays.stream(result.getAlbums().getItems()).map(AlbumSimplified::getName).toList()
                    : List.of();

            return Arrays.asList(
                    tracks.stream(),
                    artists.stream(),
                    albums.stream()
            ).stream().flatMap(s -> s).distinct().limit(limit).collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
}
