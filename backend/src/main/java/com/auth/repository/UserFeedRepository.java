package com.auth.repository;

import com.auth.model.UserFeed;
import com.auth.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFeedRepository extends JpaRepository<UserFeed, Long> {
    Page<UserFeed> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // Delete all tweets of the unfollowed user from the feed
    void deleteByUserIdAndTweetUserId(Long followerId, Long unfollowedUserId);
}
