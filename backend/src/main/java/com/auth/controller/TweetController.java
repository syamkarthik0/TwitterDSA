package com.auth.controller;

import com.auth.model.Tweet;
import com.auth.service.TweetService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tweets")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TweetController {
    
    @Autowired
    private TweetService tweetService;

    @Data
    public static class TweetRequest {
        private String content;
    }

    @PostMapping
    public ResponseEntity<?> createTweet(
        @RequestBody TweetRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            Tweet tweet = tweetService.createTweet(
                request.getContent(),
                userDetails.getUsername()
            );
            return ResponseEntity.ok(tweet);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to create tweet"));
        }
    }

    @GetMapping
    public ResponseEntity<List<Tweet>> getTweets(
        @RequestParam(required = false) Long startId,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Boolean newer
    ) {
        List<Tweet> tweets;
        if (startId == null) {
            tweets = tweetService.getInitialTweets(size);
        } else {
            tweets = Boolean.TRUE.equals(newer)
                ? tweetService.getNewerTweets(startId, size)
                : tweetService.getOlderTweets(startId, size);
        }
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserTweets(
        @PathVariable String username,
        @RequestParam(required = false) Long startId,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Boolean newer
    ) {
        try {
            List<Tweet> tweets;
            if (startId == null) {
                tweets = tweetService.getInitialUserTweets(username, size);
            } else {
                tweets = Boolean.TRUE.equals(newer)
                    ? tweetService.getNewerUserTweets(username, startId, size)
                    : tweetService.getOlderUserTweets(username, startId, size);
            }
            return ResponseEntity.ok(tweets);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
