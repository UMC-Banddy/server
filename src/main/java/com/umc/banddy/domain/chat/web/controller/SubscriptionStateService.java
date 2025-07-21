package com.umc.banddy.domain.chat.web.controller;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SubscriptionStateService {
    // sessionId → true(구독), false(구독 해제 or 초기)
    private final ConcurrentMap<String, Boolean> stateMap = new ConcurrentHashMap<>();

    public void setConnected(String sessionId) {
        stateMap.put(sessionId, false);
    }
    public void setSubscribed(String sessionId) {
        stateMap.put(sessionId, true);
    }
    public void setUnsubscribed(String sessionId) {
        stateMap.put(sessionId, false);
    }
    public boolean isSubscribed(String sessionId) {
        return stateMap.getOrDefault(sessionId, false);
    }
    public void removeSession(String sessionId) {
        stateMap.remove(sessionId);
    }
}
