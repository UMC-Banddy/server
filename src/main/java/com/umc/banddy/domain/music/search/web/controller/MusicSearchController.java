package com.umc.banddy.domain.music.search.web.controller;

import com.umc.banddy.domain.music.search.service.MusicSearchService;
import com.umc.banddy.domain.music.search.web.dto.AlbumInfo;
import com.umc.banddy.domain.music.search.web.dto.ArtistInfo;
import com.umc.banddy.domain.music.search.web.dto.SearchAllResult;
import com.umc.banddy.domain.music.search.web.dto.TrackInfo;
import com.umc.banddy.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "음악 검색", description = "음악 검색 관련 API")
@RestController
@RequestMapping("/api/music/search")
public class MusicSearchController {

    private final MusicSearchService searchService;

    @Autowired
    public MusicSearchController(MusicSearchService searchService) {
        this.searchService = searchService;
    }


    @Operation(summary = "곡 검색", description = "Spotify에서 곡을 검색합니다.")
    @GetMapping("/tracks")
    public ApiResponse<List<TrackInfo>> searchTrack(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        List<TrackInfo> result = searchService.searchTracks(q, limit, offset);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "아티스트 검색", description = "Spotify에서 아티스트를 검색합니다.")
    @GetMapping("/artists")
    public ApiResponse<List<ArtistInfo>> searchArtist(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        List<ArtistInfo> result = searchService.searchArtists(q, limit, offset);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "앨범 검색", description = "Spotify에서 앨범을 검색합니다.")
    @GetMapping("/albums")
    public ApiResponse<List<AlbumInfo>> searchAlbum(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        List<AlbumInfo> result = searchService.searchAlbums(q, limit, offset);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "음악 검색", description = "Spotify에서 곡, 아티스트, 앨범을 검색합니다.")
    @GetMapping("")
    public ApiResponse<SearchAllResult> searchAll(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        SearchAllResult result = searchService.searchAll(q, limit, offset);
        return ApiResponse.onSuccess(result);
    }
}