package com.auth.controller;

import com.auth.model.User;
import com.auth.service.SocialGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v2/social")
public class SocialGraphController {
    
    @Autowired
    private SocialGraphService socialGraphService;

    @PostMapping("/follow/{followingId}")
    public ResponseEntity<?> followUser(
            @RequestAttribute("userId") Long followerId,
            @PathVariable Long followingId) {
        try {
            socialGraphService.followUser(followerId, followingId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/unfollow/{followingId}")
    public ResponseEntity<?> unfollowUser(
            @RequestAttribute("userId") Long followerId,
            @PathVariable Long followingId) {
        try {
            socialGraphService.unfollowUser(followerId, followingId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/followers")
    public ResponseEntity<Set<User>> getFollowers(
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(socialGraphService.getFollowers(userId));
    }

    @GetMapping("/following")
    public ResponseEntity<Set<User>> getFollowing(
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(socialGraphService.getFollowing(userId));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<Set<User>> getSuggestedUsers(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "5") int maxSuggestions) {
        return ResponseEntity.ok(socialGraphService.getSuggestedUsers(userId, maxSuggestions));
    }

    @GetMapping("/feed-users")
    public ResponseEntity<List<Long>> getFeedUsers(
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(socialGraphService.getFeedUsers(userId));
    }
}
