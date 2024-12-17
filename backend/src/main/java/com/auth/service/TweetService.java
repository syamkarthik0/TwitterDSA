package com.auth.service;

import com.auth.model.Tweet;
import com.auth.model.User;
import com.auth.model.UserFeed;
import com.auth.repository.TweetRepository;
import com.auth.repository.UserRepository;
import com.auth.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class TweetService {
    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedService feedService;

    @Transactional
    public Tweet createTweet(String content, String username) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Tweet content cannot be empty");
        }
        if (content.length() > 280) {
            throw new IllegalArgumentException("Tweet content cannot exceed 280 characters");
        }

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Tweet tweet = new Tweet();
        tweet.setContent(content.trim());
        tweet.setTimestamp(LocalDateTime.now());
        tweet.setUser(user);

        tweet = tweetRepository.save(tweet);
        
        // Add tweet to followers' feeds
        feedService.addTweetToFeeds(tweet);

        return tweet;
    }

    public Page<Tweet> getUserTweets(String username, int page, int size) {
        if (size > 50) {
            size = 50; // Limit maximum page size
        }
        return tweetRepository.findByUserUsername(
            username,
            PageRequest.of(page, size)
        );
    }

    public Page<Tweet> getTweets(int page, int size) {
        if (size > 50) {
            size = 50; // Limit maximum page size
        }
        return tweetRepository.findAllByOrderByTimestampDesc(
            PageRequest.of(page, size)
        );
    }

    public Page<Tweet> getUserFeed(Long userId, int page, int size) {
        if (size > 50) {
            size = 50; // Limit maximum page size
        }
        return feedService.getUserFeed(userId, PageRequest.of(page, size))
            .map(UserFeed::getTweet);
    }
}