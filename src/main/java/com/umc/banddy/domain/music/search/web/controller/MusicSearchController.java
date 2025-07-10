package com.umc.banddy.domain.music.search.web.controller;

import com.umc.banddy.domain.music.search.service.MusicSearchService;
import com.umc.banddy.domain.music.search.web.dto.AlbumInfo;
import com.umc.banddy.domain.music.search.web.dto.ArtistInfo;
import com.umc.banddy.domain.music.search.web.dto.SearchAllResult;
import com.umc.banddy.domain.music.search.web.dto.TrackInfo;
import com.umc.banddy.global.apiPayload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/music/search")
public class MusicSearchController {

    private final MusicSearchService searchService;

    @Autowired
    public MusicSearchController(MusicSearchService searchService) {
        this.searchService = searchService;
    }


    @GetMapping("/track")
    public ApiResponse<List<TrackInfo>> searchTrack(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        List<TrackInfo> result = searchService.searchTracks(q, limit, offset);
        return ApiResponse.onSuccess(result);
    }



    @GetMapping("/artist")
    public ApiResponse<List<ArtistInfo>> searchArtist(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        List<ArtistInfo> result = searchService.searchArtists(q, limit, offset);
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/album")
    public ApiResponse<List<AlbumInfo>> searchAlbum(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        List<AlbumInfo> result = searchService.searchAlbums(q, limit, offset);
        return ApiResponse.onSuccess(result);
    }

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