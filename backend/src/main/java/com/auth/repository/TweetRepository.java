package com.auth.repository;

import com.auth.model.Tweet;
import com.auth.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    Page<Tweet> findByUserOrderByTimestampDesc(User user, Pageable pageable);
    
    @Query("SELECT t FROM Tweet t ORDER BY t.timestamp DESC")
    List<Tweet> findLatestTweets(Pageable pageable);
    
    @Query("SELECT t FROM Tweet t WHERE t.id < :startId ORDER BY t.id DESC")
    List<Tweet> findOlderTweets(@Param("startId") Long startId, Pageable pageable);
    
    @Query("SELECT t FROM Tweet t WHERE t.id > :startId ORDER BY t.id ASC")
    List<Tweet> findNewerTweets(@Param("startId") Long startId, Pageable pageable);
    
    @Query("SELECT t FROM Tweet t WHERE t.user = :user AND t.id < :startId ORDER BY t.id DESC")
    List<Tweet> findOlderTweetsByUser(@Param("user") User user, @Param("startId") Long startId, Pageable pageable);
    
    @Query("SELECT t FROM Tweet t WHERE t.user = :user AND t.id > :startId ORDER BY t.id ASC")
    List<Tweet> findNewerTweetsByUser(@Param("user") User user, @Param("startId") Long startId, Pageable pageable);
}
