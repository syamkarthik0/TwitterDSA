package com.auth.controller;

import com.auth.model.User;
import com.auth.security.JwtUtil;
import com.auth.service.AuthService;
import com.auth.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SessionManager sessionManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = authService.register(user);
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(registeredUser.getUsername())
                .password("")
                .authorities(registeredUser.getRole())
                .build();

            String token = jwtUtil.generateToken(userDetails);
            sessionManager.createSession(registeredUser.getUsername(), token);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("token", token);
            response.put("username", registeredUser.getUsername());
            response.put("role", registeredUser.getRole());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        if (!credentials.containsKey("username") || !credentials.containsKey("password")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
        }

        String username = credentials.get("username");
        String password = credentials.get("password");

        try {
            Optional<User> userOpt = authService.authenticate(username, password);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(username)
                    .password("")
                    .authorities(user.getRole())
                    .build();

                String token = jwtUtil.generateToken(userDetails);
                sessionManager.createSession(username, token);

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("username", username);
                response.put("role", user.getRole());
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or missing token"));
        }

        try {
            String jwt = token.substring(7);
            String username = jwtUtil.extractUsername(jwt);
            
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("")
                .authorities("USER")
                .build();

            if (!jwtUtil.validateToken(jwt, userDetails)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired token"));
            }

            authService.logout(username);
            return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Logout failed: " + e.getMessage()));
        }
    }
}
