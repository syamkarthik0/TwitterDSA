package com.auth.controller;

import com.auth.model.Tweet;
import com.auth.service.TweetService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/tweets")
@CrossOrigin(origins = "http://localhost:3000")
public class TweetController {
    
    @Autowired
    private TweetService tweetService;

    @Data
    public static class TweetRequest {
        private String content;
    }

    @GetMapping
    public ResponseEntity<Page<Tweet>> getTweets(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(tweetService.getTweets(page, size));
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

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserTweets(
        @PathVariable String username,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<Tweet> tweets = tweetService.getUserTweets(username, page, size);
            return ResponseEntity.ok(tweets);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
