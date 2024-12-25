package com.auth.repository;

import com.auth.model.User;
import com.auth.model.UserFeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFeedRepository extends JpaRepository<UserFeed, Long> {
    Page<UserFeed> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    @Modifying
    @Query("DELETE FROM UserFeed uf WHERE uf.user.id = :followerId AND uf.tweet.user.id = :unfollowedUserId")
    int deleteByUserAndTweetUser(@Param("followerId") Long followerId, @Param("unfollowedUserId") Long unfollowedUserId);

    // Add a method to find feed entries to delete
    List<UserFeed> findByUserIdAndTweetUserId(Long userId, Long tweetUserId);
}
