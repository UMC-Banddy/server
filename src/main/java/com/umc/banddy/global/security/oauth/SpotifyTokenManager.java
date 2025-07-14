package com.umc.banddy.global.security.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

@Component
public class SpotifyTokenManager {

    private SpotifyApi spotifyApi;
    private String accessToken;
    private long expiresAt = 0;

    public SpotifyTokenManager(
            @Value("${spotify.client-id}") String clientId,
            @Value("${spotify.client-secret}") String clientSecret
    ) {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
    }

    public synchronized String getAccessToken() {
        long now = System.currentTimeMillis();
        if (accessToken == null || now > expiresAt) {
            refreshToken();
        }
        return accessToken;
    }

    private void refreshToken() {
        try {
            ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
            ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            this.accessToken = clientCredentials.getAccessToken();
            this.expiresAt = System.currentTimeMillis() + (clientCredentials.getExpiresIn() - 60) * 1000L; // 1분 여유
            spotifyApi.setAccessToken(this.accessToken);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Spotify access token: " + e.getMessage(), e);
        }
    }

    public SpotifyApi getSpotifyApi() {
        // 항상 최신 토큰이 들어간 SpotifyApi 반환
        spotifyApi.setAccessToken(getAccessToken());
        return spotifyApi;
    }
}
