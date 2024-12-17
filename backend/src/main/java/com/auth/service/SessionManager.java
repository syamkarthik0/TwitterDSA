package com.auth.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SessionManager {
    private final Map<String, String> sessions = new HashMap<>();

    public void createSession(String username, String token) {
        sessions.put(username, token);
    }

    public Optional<String> getSession(String username) {
        return Optional.ofNullable(sessions.get(username));
    }

    public void invalidateSession(String username) {
        sessions.remove(username);
    }

    public boolean isSessionValid(String username, String token) {
        String storedToken = sessions.get(username);
        return storedToken != null && storedToken.equals(token);
    }
}
