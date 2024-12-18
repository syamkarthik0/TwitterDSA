package com.auth.graph;

import com.auth.model.User;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
public class SocialGraph {
    // Adjacency list representation using ConcurrentHashMap for thread safety
    private final Map<Long, Set<Long>> followers;
    private final Map<Long, Set<Long>> following;
    private final Map<Long, User> users;

    public SocialGraph() {
        this.followers = new ConcurrentHashMap<>();
        this.following = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
    }

    // Add a user to the graph
    public synchronized void addUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User or user ID cannot be null");
        }

        Long userId = user.getId();
        users.putIfAbsent(userId, user);
        followers.putIfAbsent(userId, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        following.putIfAbsent(userId, Collections.newSetFromMap(new ConcurrentHashMap<>()));
    }

    // Add a following relationship (edge)
    public synchronized void addFollowing(Long followerId, Long followingId) {
        if (!users.containsKey(followerId)) {
            throw new IllegalArgumentException("Follower not found in graph");
        }
        if (!users.containsKey(followingId)) {
            throw new IllegalArgumentException("Following user not found in graph");
        }

        // Add to following set of follower
        following.get(followerId).add(followingId);
        // Add to followers set of following
        followers.get(followingId).add(followerId);
    }

    // Remove a following relationship (edge)
    public synchronized void removeFollowing(Long followerId, Long followingId) {
        if (!users.containsKey(followerId)) {
            throw new IllegalArgumentException("Follower not found in graph");
        }
        if (!users.containsKey(followingId)) {
            throw new IllegalArgumentException("Following user not found in graph");
        }

        // Remove from following set of follower
        following.get(followerId).remove(followingId);
        // Remove from followers set of following
        followers.get(followingId).remove(followerId);
    }

    // Get all followers of a user
    public Set<User> getFollowers(Long userId) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("User not found in graph");
        }

        return followers.get(userId).stream()
            .map(users::get)
            .collect(Collectors.toSet());
    }

    // Get all users that a user is following
    public Set<User> getFollowing(Long userId) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("User not found in graph");
        }

        return following.get(userId).stream()
            .map(users::get)
            .collect(Collectors.toSet());
    }

    // Check if one user follows another
    public boolean isFollowing(Long followerId, Long followingId) {
        if (!users.containsKey(followerId) || !users.containsKey(followingId)) {
            return false;
        }
        return following.get(followerId).contains(followingId);
    }

    // Get suggested users (friends of friends)
    public Set<User> getSuggestedUsers(Long userId, int maxSuggestions) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("User not found in graph");
        }

        Set<Long> suggestedIds = new HashSet<>();
        Set<Long> alreadyFollowing = following.get(userId);

        // Add friends of friends
        for (Long followingId : following.get(userId)) {
            suggestedIds.addAll(following.get(followingId));
        }

        // Remove the user themselves and users they already follow
        suggestedIds.remove(userId);
        suggestedIds.removeAll(alreadyFollowing);

        // Convert to list, shuffle, and take top N
        List<Long> shuffledIds = new ArrayList<>(suggestedIds);
        Collections.shuffle(shuffledIds);
        return shuffledIds.stream()
            .limit(maxSuggestions)
            .map(users::get)
            .collect(Collectors.toSet());
    }
}
