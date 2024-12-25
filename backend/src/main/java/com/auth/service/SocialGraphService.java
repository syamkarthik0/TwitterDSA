package com.auth.service;

import com.auth.graph.SocialGraph;
import com.auth.model.User;
import com.auth.model.Tweet;
import com.auth.repository.UserRepository;
import com.auth.service.FeedService;
import com.auth.service.UserRelationshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SocialGraphService {
    private static final Logger logger = LoggerFactory.getLogger(SocialGraphService.class);
    
    private final SocialGraph socialGraph;
    private final UserRepository userRepository;
    private final FeedService feedService;
    private final UserRelationshipService userRelationshipService;

    @Autowired
    public SocialGraphService(SocialGraph socialGraph, UserRepository userRepository, 
                            FeedService feedService, UserRelationshipService userRelationshipService) {
        this.socialGraph = socialGraph;
        this.userRepository = userRepository;
        this.feedService = feedService;
        this.userRelationshipService = userRelationshipService;
        initializeGraph();
    }

    // Initialize graph with existing users from database
    @PostConstruct
    @Transactional(readOnly = true)
    private void initializeGraph() {
        try {
            // Load all users from database
            List<User> users = userRepository.findAll();
            
            // Add all users to graph
            for (User user : users) {
                socialGraph.addUser(user);
            }

            // Add all following relationships
            for (User user : users) {
                List<User> following = userRelationshipService.getFollowing(user.getId());
                for (User followedUser : following) {
                    socialGraph.addFollowing(user.getId(), followedUser.getId());
                }
            }
            logger.info("Successfully initialized social graph with {} users", users.size());
        } catch (Exception e) {
            logger.error("Error initializing social graph: {}", e.getMessage());
            throw e;
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
        logger.info("Starting unfollow process - Follower: {}, Unfollowing: {}", followerId, followingId);
        
        if (followerId.equals(followingId)) {
            logger.warn("Attempted to unfollow self - User: {}", followerId);
            throw new IllegalArgumentException("Users cannot unfollow themselves");
        }
        
        User follower = userRepository.findById(followerId)
            .orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User following = userRepository.findById(followingId)
            .orElseThrow(() -> new IllegalArgumentException("Following user not found"));

        // Check if not following
        if (!socialGraph.isFollowing(followerId, followingId)) {
            logger.warn("Not following user - Follower: {}, Target: {}", followerId, followingId);
            return; // Not following, no need to do anything
        }

        try {
            // Remove from graph
            socialGraph.removeFollowing(followerId, followingId);

            // Update database relationships
            follower.getFollowing().remove(following);
            following.getFollowers().remove(follower);
            userRepository.save(follower);

            // Clean up the feed
            feedService.removeUserTweetsFromFeed(followerId, followingId);
            
            logger.info("Successfully unfollowed user - Follower: {}, Unfollowed: {}", followerId, followingId);
        } catch (Exception e) {
            logger.error("Error during unfollow process: {}", e.getMessage());
            throw new RuntimeException("Failed to unfollow user", e);
        }
    }

    private void validateUsers(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
            .orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User following = userRepository.findById(followingId)
            .orElseThrow(() -> new IllegalArgumentException("Following user not found"));

        // Check if not following
        if (!socialGraph.isFollowing(followerId, followingId)) {
            return; // Not following, no need to do anything
        }

        // Update database
        follower.getFollowing().remove(following);
        following.getFollowers().remove(follower);
        
        // Save both users to persist the relationship changes
        userRepository.save(follower);
        userRepository.save(following);
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

    // Get user's followers as a list
    public List<User> getFollowersList(Long userId) {
        return userRelationshipService.getFollowers(userId);
    }

    // Get users that a user is following as a list
    public List<User> getFollowingList(Long userId) {
        return userRelationshipService.getFollowing(userId);
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
