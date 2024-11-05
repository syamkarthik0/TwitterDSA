package com.auth.service;

import com.auth.model.User;
import com.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SessionManager sessionManager;

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Set default role if not specified
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        
        // Encrypt password before storing
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return userOpt;
        }
        
        return Optional.empty();
    }

    public void logout(String username) {
        sessionManager.invalidateSession(username);
    }
}
