package com.auth.service;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionManager {
    private final Map<String, String> userSessions = new HashMap<>();

    public void createSession(String username, String token) {
        userSessions.put(username, token);
    }

    public void invalidateSession(String username) {
        userSessions.remove(username);
    }

    public boolean isSessionValid(String username, String token) {
        String storedToken = userSessions.get(username);
        return storedToken != null && storedToken.equals(token);
    }
}
