package com.auth.service;

import com.auth.model.*;
import com.auth.repository.UserFeedRepository;
import com.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedService {
    @Autowired
    private UserFeedRepository userFeedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowService followService;

    @Transactional
    public void addTweetToFeeds(Tweet tweet) {
        // Get all followers of the tweet's author
        List<User> followers = followService.getFollowers(tweet.getUser().getId());
        
        // Add tweet to each follower's feed
        for (User follower : followers) {
            UserFeed feedItem = new UserFeed();
            feedItem.setUser(follower);
            feedItem.setTweet(tweet);
            feedItem.setCreatedAt(LocalDateTime.now());
            userFeedRepository.save(feedItem);
        }

        // Also add to the author's own feed
        UserFeed authorFeed = new UserFeed();
        authorFeed.setUser(tweet.getUser());
        authorFeed.setTweet(tweet);
        authorFeed.setCreatedAt(LocalDateTime.now());
        userFeedRepository.save(authorFeed);
    }

    public Page<UserFeed> getUserFeed(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userFeedRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
}
