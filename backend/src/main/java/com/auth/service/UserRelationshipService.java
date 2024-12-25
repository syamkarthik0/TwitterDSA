package com.auth.service;

import com.auth.model.User;
import com.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRelationshipService {
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> getFollowers(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new ArrayList<>(user.getFollowers());
    }

    @Transactional(readOnly = true)
    public List<User> getFollowing(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new ArrayList<>(user.getFollowing());
    }

    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
