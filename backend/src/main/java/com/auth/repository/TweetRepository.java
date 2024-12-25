package com.auth.repository;

import com.auth.model.Tweet;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    Page<Tweet> findByUserUsername(String username, Pageable pageable);
    Page<Tweet> findAllByOrderByTimestampDesc(Pageable pageable);
    List<Tweet> findByUserId(Long userId);
}
