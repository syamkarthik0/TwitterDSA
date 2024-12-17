package com.auth.repository;

import com.auth.model.Follower;
import com.auth.model.FollowerId;
import com.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {
    @Query("SELECT f.follower FROM Follower f WHERE f.following.id = ?1")
    List<User> findFollowersByFollowingId(Long followingId);

    @Query("SELECT f.following FROM Follower f WHERE f.follower.id = ?1")
    List<User> findFollowingByFollowerId(Long followerId);
    
    @Query("SELECT COUNT(f) > 0 FROM Follower f WHERE f.follower.id = ?1 AND f.following.id = ?2")
    boolean isFollowing(Long followerId, Long followingId);
}
