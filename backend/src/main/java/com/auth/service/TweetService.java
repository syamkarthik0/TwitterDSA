package com.auth.service;

import com.auth.model.Tweet;
import com.auth.model.User;
import com.auth.repository.TweetRepository;
import com.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TweetService {
    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

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

        return tweetRepository.save(tweet);
    }

    public List<Tweet> getInitialTweets(int size) {
        if (size > 50) size = 50; // Limit maximum size
        return tweetRepository.findLatestTweets(PageRequest.of(0, size));
    }

    public List<Tweet> getOlderTweets(Long startId, int size) {
        if (size > 50) size = 50;
        return tweetRepository.findOlderTweets(startId, PageRequest.of(0, size));
    }

    public List<Tweet> getNewerTweets(Long startId, int size) {
        if (size > 50) size = 50;
        return tweetRepository.findNewerTweets(startId, PageRequest.of(0, size));
    }

    public List<Tweet> getInitialUserTweets(String username, int size) {
        if (size > 50) size = 50;
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return tweetRepository.findByUserOrderByTimestampDesc(user, PageRequest.of(0, size))
            .getContent();
    }

    public List<Tweet> getOlderUserTweets(String username, Long startId, int size) {
        if (size > 50) size = 50;
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return tweetRepository.findOlderTweetsByUser(user, startId, PageRequest.of(0, size));
    }

    public List<Tweet> getNewerUserTweets(String username, Long startId, int size) {
        if (size > 50) size = 50;
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return tweetRepository.findNewerTweetsByUser(user, startId, PageRequest.of(0, size));
    }
}
