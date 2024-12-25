package com.auth.service;

import com.auth.graph.Graph;
import com.auth.model.User;
import com.auth.repository.UserRepository;
import com.auth.service.FeedService;
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
    
    private final Graph followingGraph;  // Tracks who each user follows
    private final Graph followersGraph;  // Tracks who follows each user
    private final UserRepository userRepository;
    private final FeedService feedService;

    @Autowired
    public SocialGraphService(UserRepository userRepository, FeedService feedService) {
        this.followingGraph = new Graph();
        this.followersGraph = new Graph();
        this.userRepository = userRepository;
        this.feedService = feedService;
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
                followingGraph.addNode(user.getId());
                followersGraph.addNode(user.getId());
            }

            // Add all following relationships
            for (User user : users) {
                for (User following : user.getFollowing()) {
                    followingGraph.addEdge(user.getId(), following.getId());
                    followersGraph.addEdge(following.getId(), user.getId());
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
        if (followingGraph.hasEdge(followerId, followingId)) {
            return; // Already following, no need to do anything
        }

        // Update both graphs
        followingGraph.addEdge(followerId, followingId);
        followersGraph.addEdge(followingId, followerId);

        // Update database relationships
        follower.getFollowing().add(following);
        following.getFollowers().add(follower);
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
        if (!followingGraph.hasEdge(followerId, followingId)) {
            logger.warn("Not following user - Follower: {}, Target: {}", followerId, followingId);
            throw new IllegalStateException("Not following this user");
        }

        // Update both graphs
        followingGraph.removeEdge(followerId, followingId);
        followersGraph.removeEdge(followingId, followerId);

        // Update database relationships
        follower.getFollowing().remove(following);
        following.getFollowers().remove(follower);
        userRepository.save(follower);
        userRepository.save(following);

        try {
            // Clean up the feed in a separate transaction
            feedService.removeUserTweetsFromFeed(followerId, followingId);
        } catch (Exception e) {
            // Log but continue - feed cleanup failure shouldn't prevent unfollow
            logger.warn("Feed cleanup failed but unfollow succeeded: {}", e.getMessage());
        }
        
        logger.info("Successfully unfollowed user - Follower: {}, Unfollowed: {}", followerId, followingId);
    }

    // Get user's followers
    public Set<User> getFollowers(Long userId) {
        Set<Long> followerIds = followersGraph.getNeighbors(userId);
        return followerIds.stream()
            .map(id -> userRepository.findById(id).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    // Get users that a user is following
    public Set<User> getFollowing(Long userId) {
        Set<Long> followingIds = followingGraph.getNeighbors(userId);
        return followingIds.stream()
            .map(id -> userRepository.findById(id).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    // Get user's followers as a list
    public List<User> getFollowersList(Long userId) {
        return new ArrayList<>(getFollowers(userId));
    }

    // Get users that a user is following as a list
    public List<User> getFollowingList(Long userId) {
        return new ArrayList<>(getFollowing(userId));
    }

    // Check if one user follows another
    public boolean isFollowing(Long followerId, Long followingId) {
        return followingGraph.hasEdge(followerId, followingId);
    }

    // Get suggested users to follow
    public Set<User> getSuggestedUsers(Long userId, int maxSuggestions) {
        Set<Long> suggestedIds = followingGraph.getTwoHopNodes(userId);
        
        // Convert IDs to Users and limit the number of suggestions
        return suggestedIds.stream()
            .map(id -> userRepository.findById(id).orElse(null))
            .filter(Objects::nonNull)
            .limit(maxSuggestions)
            .collect(Collectors.toSet());
    }

    // Get mutual connections between users
    public Set<User> getMutualConnections(Long user1Id, Long user2Id) {
        Set<Long> mutualIds = followingGraph.getMutualConnections(user1Id, user2Id);
        
        return mutualIds.stream()
            .map(id -> userRepository.findById(id).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    // Get users for feed generation (user + following)
    public Set<User> getFeedUsers(Long userId) {
        Set<User> feedUsers = new HashSet<>(getFollowing(userId));
        userRepository.findById(userId).ifPresent(feedUsers::add);
        return feedUsers;
    }
}
