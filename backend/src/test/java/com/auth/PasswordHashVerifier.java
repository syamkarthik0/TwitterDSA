package com.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashVerifier {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String storedHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        String rawPassword = "test123";
        
        boolean matches = encoder.matches(rawPassword, storedHash);
        System.out.println("Password 'test123' matches stored hash: " + matches);
        
        // Generate a new hash for the same password to compare structure
        String newHash = encoder.encode(rawPassword);
        System.out.println("New hash generated for 'test123': " + newHash);
        System.out.println("New hash matches password: " + encoder.matches(rawPassword, newHash));
    }
}
