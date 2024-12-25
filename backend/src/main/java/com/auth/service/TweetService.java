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
    public Tweet createTweet(Long userId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Tweet content cannot be empty");
        }
        if (content.length() > 280) {
            throw new IllegalArgumentException("Tweet content cannot exceed 280 characters");
        }

        User user = userRepository.findById(userId)
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

    public Page<Tweet> getUserTweets(Long userId, int page, int size) {
        if (size > 50) {
            size = 50; // Limit maximum page size
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        return tweetRepository.findByUserOrderByTimestampDesc(
            user,
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

    @Transactional(readOnly = true)
    public Page<Tweet> getFeedForUser(Long userId, int page, int size) {
        return feedService.getFeedForUser(userId, PageRequest.of(page, size));
    }
    
    @Transactional
    public void deleteTweet(Long userId, Long tweetId) {
        Tweet tweet = tweetRepository.findById(tweetId)
            .orElseThrow(() -> new RuntimeException("Tweet not found"));
            
        if (!tweet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this tweet");
        }
        
        tweetRepository.delete(tweet);
    }
}