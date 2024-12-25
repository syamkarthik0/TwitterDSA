package com.auth.controller;

import com.auth.model.User;
import com.auth.service.SocialGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follow")
@CrossOrigin(origins = "http://localhost:3000")
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    private SocialGraphService socialGraphService;

    @PostMapping("/{followingId}")
    public ResponseEntity<?> followUser(HttpServletRequest request, @PathVariable Long followingId) {
        try {
            Long followerId = (Long) request.getAttribute("userId");
            logger.info("Follow request - Follower ID: {}, Following ID: {}", followerId, followingId);
            
            if (followerId == null) {
                logger.error("Follower ID is missing in the request");
                return ResponseEntity.badRequest().body(Map.of("error", "Not authenticated"));
            }

            socialGraphService.followUser(followerId, followingId);
            return ResponseEntity.ok(Map.of("message", "Successfully followed user"));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid follow request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.error("Follow state error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error in followUser: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @DeleteMapping("/{followingId}")
    public ResponseEntity<?> unfollowUser(HttpServletRequest request, @PathVariable Long followingId) {
        try {
            Long followerId = (Long) request.getAttribute("userId");
            logger.info("Unfollow request - Follower ID: {}, Following ID: {}", followerId, followingId);
            
            if (followerId == null) {
                logger.error("Follower ID is missing in the request");
                return ResponseEntity.badRequest().body(Map.of("error", "Not authenticated"));
            }

            if (followerId.equals(followingId)) {
                logger.error("User cannot unfollow themselves");
                return ResponseEntity.badRequest().body(Map.of("error", "You cannot unfollow yourself"));
            }

            socialGraphService.unfollowUser(followerId, followingId);
            return ResponseEntity.ok(Map.of("message", "Successfully unfollowed user"));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid unfollow request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.error("Unfollow state error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error in unfollowUser: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<User>> getFollowers(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(socialGraphService.getFollowersList(userId));
        } catch (Exception e) {
            logger.error("Error getting followers for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<List<User>> getFollowing(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(socialGraphService.getFollowingList(userId));
        } catch (Exception e) {
            logger.error("Error getting following for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/check/{userId}/{followingId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long userId, @PathVariable Long followingId) {
        try {
            return ResponseEntity.ok(socialGraphService.isFollowing(userId, followingId));
        } catch (Exception e) {
            logger.error("Error checking follow status for {} following {}: {}", userId, followingId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
