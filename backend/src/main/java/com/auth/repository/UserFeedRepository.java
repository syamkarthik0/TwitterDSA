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

@Repository
public interface UserFeedRepository extends JpaRepository<UserFeed, Long> {
    Page<UserFeed> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    @Modifying
    @Query(value = "DELETE FROM user_feeds WHERE user_id = :followerId AND tweet_id IN (SELECT id FROM tweets WHERE user_id = :unfollowedUserId)", nativeQuery = true)
    int deleteByUserAndTweetUser(@Param("followerId") Long followerId, @Param("unfollowedUserId") Long unfollowedUserId);
}
