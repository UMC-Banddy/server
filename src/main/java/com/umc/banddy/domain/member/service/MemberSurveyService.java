package com.umc.banddy.domain.member.service;

import com.umc.banddy.domain.member.domain.Genre;
import com.umc.banddy.domain.member.web.dto.MemberSurveyRequest;
import com.umc.banddy.domain.music.artist.domain.Artist;
import com.umc.banddy.domain.member.enums.KeywordCategory;
import com.umc.banddy.domain.member.web.dto.SimpleKeywordDto;
import com.umc.banddy.domain.member.web.dto.SimpleSessionDto;
import java.util.List;
import java.util.Map;

public interface MemberSurveyService {

    void saveSurveyInfo(String accessToken, MemberSurveyRequest request);

    List<Genre> getAllGenres();
    List<Artist> getAllArtists();
    List<Genre> searchGenres(String keyword);
    List<Artist> searchArtists(String keyword);
    Map<KeywordCategory, List<SimpleKeywordDto>> getGroupedKeywords();
    List<SimpleSessionDto> getAllSessions();
}
