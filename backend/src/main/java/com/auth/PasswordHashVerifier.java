package com.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashVerifier {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Test existing user
        String storedHash = "$2a$10$xXG12g53IPX.K4/HN7OGjez9743qlhgbvSQNvt6Sc5dMiayzmrlbW";
        String rawPassword = "test123";
        
        boolean matches = encoder.matches(rawPassword, storedHash);
        System.out.println("Password 'test123' matches stored hash: " + matches);
        
        // Generate hash for new user
        String shashankPassword = "shashank";
        String shashankHash = encoder.encode(shashankPassword);
        System.out.println("\nNew hash generated for 'shashank': " + shashankHash);
        System.out.println("Verifying new hash matches password: " + encoder.matches(shashankPassword, shashankHash));
    }
}
