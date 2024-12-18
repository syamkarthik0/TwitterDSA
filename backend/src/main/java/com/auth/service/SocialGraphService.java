package com.auth.service;

import com.auth.graph.SocialGraph;
import com.auth.model.User;
import com.auth.model.Tweet;
import com.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SocialGraphService {
    private final SocialGraph socialGraph;
    private final UserRepository userRepository;

    @Autowired
    public SocialGraphService(SocialGraph socialGraph, UserRepository userRepository) {
        this.socialGraph = socialGraph;
        this.userRepository = userRepository;
        initializeGraph();
    }

    // Initialize graph with existing users from database
    @PostConstruct
    @Transactional(readOnly = true)
    private void initializeGraph() {
        try {
            // Load all users and their relationships from database
            List<User> users = userRepository.findAll();
            
            // Add all users to graph
            for (User user : users) {
                socialGraph.addUser(user);
            }

            // Add all following relationships
            for (User user : users) {
                for (User following : user.getFollowing()) {
                    socialGraph.addFollowing(user.getId(), following.getId());
                }
            }
        } catch (Exception e) {
            // Log the error but don't throw it to allow the application to start
            System.err.println("Error initializing social graph: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Follow a user
    @Transactional
    public void followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("Users cannot follow themselves");
        }

        User follower = userRepository.findById(followerId)
            .orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User following = userRepository.findById(followingId)
            .orElseThrow(() -> new IllegalArgumentException("Following user not found"));

        // Check if already following
        if (socialGraph.isFollowing(followerId, followingId)) {
            return; // Already following, no need to do anything
        }

        // Add to graph first
        socialGraph.addUser(follower);
        socialGraph.addUser(following);
        socialGraph.addFollowing(followerId, followingId);

        // Update database
        follower.getFollowing().add(following);
        following.getFollowers().add(follower);
        
        // Save only the follower since the relationship is managed from the follower side
        userRepository.save(follower);
    }

    // Unfollow a user
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("Users cannot unfollow themselves");
        }

        User follower = userRepository.findById(followerId)
            .orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User following = userRepository.findById(followingId)
            .orElseThrow(() -> new IllegalArgumentException("Following user not found"));

        // Check if not following
        if (!socialGraph.isFollowing(followerId, followingId)) {
            return; // Not following, no need to do anything
        }

        // Remove from graph first
        socialGraph.removeFollowing(followerId, followingId);

        // Update database
        follower.getFollowing().remove(following);
        following.getFollowers().remove(follower);
        
        // Save only the follower since the relationship is managed from the follower side
        userRepository.save(follower);
    }

    // Get user's followers
    public Set<User> getFollowers(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        socialGraph.addUser(user); // Ensure user exists in graph
        return socialGraph.getFollowers(userId);
    }

    // Get users that a user is following
    public Set<User> getFollowing(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        socialGraph.addUser(user); // Ensure user exists in graph
        return socialGraph.getFollowing(userId);
    }

    // Check if one user follows another
    public boolean isFollowing(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
            .orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User following = userRepository.findById(followingId)
            .orElseThrow(() -> new IllegalArgumentException("Following user not found"));
        socialGraph.addUser(follower); // Ensure user exists in graph
        socialGraph.addUser(following); // Ensure user exists in graph
        return socialGraph.isFollowing(followerId, followingId);
    }

    // Get suggested users to follow
    public Set<User> getSuggestedUsers(Long userId, int maxSuggestions) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        socialGraph.addUser(user); // Ensure user exists in graph
        return socialGraph.getSuggestedUsers(userId, maxSuggestions);
    }

    // Get users for feed generation
    public List<Long> getFeedUsers(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        socialGraph.addUser(user); // Ensure user exists in graph
        List<Long> feedUsers = new ArrayList<>(socialGraph.getFollowing(userId).stream()
            .map(User::getId)
            .collect(Collectors.toList()));
        feedUsers.add(userId); // Add the user's own tweets to the feed
        return feedUsers;
    }
}
