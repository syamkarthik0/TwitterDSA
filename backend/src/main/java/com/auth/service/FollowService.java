package com.auth.service;

import com.auth.model.*;
import com.auth.repository.FollowerRepository;
import com.auth.repository.TweetRepository;
import com.auth.repository.UserRepository;
import com.auth.repository.UserFeedRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowService {
    private static final Logger logger = LoggerFactory.getLogger(FollowService.class);

    @Autowired
    private FollowerRepository followerRepository;

    @Autowired
    private TweetRepository tweetRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFeedRepository userFeedRepository;



    @Transactional
    public void followUser(Long followerId, Long followingId) {
        logger.debug("Attempting to follow - Follower ID: {}, Following ID: {}", followerId, followingId);
    
        if (followerId == null || followingId == null) {
            logger.error("Invalid follow request - Follower ID or Following ID is null");
            throw new IllegalArgumentException("Both follower and following IDs are required");
        }
    
        if (followerId.equals(followingId)) {
            logger.error("Invalid follow request - User attempting to follow themselves");
            throw new IllegalArgumentException("Users cannot follow themselves");
        }
    
        if (followerRepository.isFollowing(followerId, followingId)) {
            logger.error("Invalid follow request - Already following user");
            throw new IllegalStateException("Already following this user");
        }
    
        User follower = userRepository.findById(followerId)
            .orElseThrow(() -> {
                logger.error("Follower not found with ID: {}", followerId);
                return new IllegalArgumentException("Follower not found");
            });
    
        User following = userRepository.findById(followingId)
            .orElseThrow(() -> {
                logger.error("User to follow not found with ID: {}", followingId);
                return new IllegalArgumentException("User to follow not found");
            });
    
        Follower relationship = new Follower();
        relationship.setId(new FollowerId(followerId, followingId));
        relationship.setFollower(follower);
        relationship.setFollowing(following);
        relationship.setFollowDate(LocalDateTime.now());
    
        try {
        followerRepository.save(relationship);
        logger.info("Successfully created follow relationship - Follower: {}, Following: {}", followerId, followingId);

        // Add existing tweets to follower's feed
        List<Tweet> tweets = tweetRepository.findByUserId(followingId);  // Get all tweets of the followed user
        for (Tweet tweet : tweets) {
            UserFeed userFeed = new UserFeed(follower, tweet, LocalDateTime.now());  // Create a new UserFeed object
            userFeedRepository.save(userFeed);  // Save the new UserFeed object
        }
    
        } catch (Exception e) {
            logger.error("Error saving follow relationship: {}", e.getMessage());
            throw new RuntimeException("Failed to save follow relationship", e);
        }
    }
    

    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        logger.debug("Attempting to unfollow - Follower ID: {}, Following ID: {}", followerId, followingId);
    
        if (followerId == null || followingId == null) {
            logger.error("Invalid unfollow request - Follower ID or Following ID is null");
            throw new IllegalArgumentException("Both follower and following IDs are required");
        }
    
        if (!followerRepository.isFollowing(followerId, followingId)) {
            logger.error("Invalid unfollow request - Not following user");
            throw new IllegalStateException("Not following this user");
        }
    
        try {
            FollowerId id = new FollowerId(followerId, followingId);
            followerRepository.deleteById(id);
    
            // Remove all tweets of the unfollowed user from the follower's feed
            userFeedRepository.deleteByUserIdAndTweetUserId(followerId, followingId);
    
            logger.info("Successfully removed follow relationship and feed entries - Follower: {}, Following: {}", followerId, followingId);
        } catch (Exception e) {
            logger.error("Error removing follow relationship: {}", e.getMessage());
            throw new RuntimeException("Failed to remove follow relationship", e);
        }
    }
    
    public List<User> getFollowers(Long userId) {
        logger.debug("Getting followers for user ID: {}", userId);
        return followerRepository.findFollowersByFollowingId(userId);
    }

    public List<User> getFollowing(Long userId) {
        logger.debug("Getting following for user ID: {}", userId);
        return followerRepository.findFollowingByFollowerId(userId);
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        logger.debug("Checking if user {} is following user {}", followerId, followingId);
        return followerRepository.isFollowing(followerId, followingId);
    }
}
