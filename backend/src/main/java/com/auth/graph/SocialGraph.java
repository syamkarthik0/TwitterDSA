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
    private final DirectedGraph followGraph;
    private final Map<Long, User> users;

    public SocialGraph() {
        this.followGraph = new DirectedGraph();
        this.users = new ConcurrentHashMap<>();
    }

    // Inner class representing a thread-safe directed graph
    private static class DirectedGraph {
        private final Map<Long, Set<Long>> followers;
        private final Map<Long, Set<Long>> following;

        public DirectedGraph() {
            this.followers = new ConcurrentHashMap<>();
            this.following = new ConcurrentHashMap<>();
        }

        public synchronized void addNode(Long nodeId) {
            followers.putIfAbsent(nodeId, Collections.newSetFromMap(new ConcurrentHashMap<>()));
            following.putIfAbsent(nodeId, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        }

        public synchronized void addEdge(Long fromNode, Long toNode) {
            following.get(fromNode).add(toNode);
            followers.get(toNode).add(fromNode);
        }

        public synchronized void removeEdge(Long fromNode, Long toNode) {
            following.get(fromNode).remove(toNode);
            followers.get(toNode).remove(fromNode);
        }

        public Set<Long> getFollowers(Long nodeId) {
            return new HashSet<>(followers.getOrDefault(nodeId, Collections.emptySet()));
        }

        public Set<Long> getFollowing(Long nodeId) {
            return new HashSet<>(following.getOrDefault(nodeId, Collections.emptySet()));
        }

        public boolean hasEdge(Long fromNode, Long toNode) {
            return following.containsKey(fromNode) && following.get(fromNode).contains(toNode);
        }
    }

    // Add a user to the graph
    public synchronized void addUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User or user ID cannot be null");
        }

        Long userId = user.getId();
        users.putIfAbsent(userId, user);
        followGraph.addNode(userId);
    }

    // Add a following relationship (edge)
    public synchronized void addFollowing(Long followerId, Long followingId) {
        if (!users.containsKey(followerId)) {
            throw new IllegalArgumentException("Follower not found in graph");
        }
        if (!users.containsKey(followingId)) {
            throw new IllegalArgumentException("Following user not found in graph");
        }

        followGraph.addEdge(followerId, followingId);
    }

    // Remove a following relationship (edge)
    public synchronized void removeFollowing(Long followerId, Long followingId) {
        if (!users.containsKey(followerId)) {
            throw new IllegalArgumentException("Follower not found in graph");
        }
        if (!users.containsKey(followingId)) {
            throw new IllegalArgumentException("Following user not found in graph");
        }

        followGraph.removeEdge(followerId, followingId);
    }

    // Get all followers of a user
    public Set<User> getFollowers(Long userId) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("User not found in graph");
        }

        return followGraph.getFollowers(userId).stream()
            .map(users::get)
            .collect(Collectors.toSet());
    }

    // Get all users that a user is following
    public Set<User> getFollowing(Long userId) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("User not found in graph");
        }

        return followGraph.getFollowing(userId).stream()
            .map(users::get)
            .collect(Collectors.toSet());
    }

    // Check if one user follows another
    public boolean isFollowing(Long followerId, Long followingId) {
        if (!users.containsKey(followerId) || !users.containsKey(followingId)) {
            return false;
        }
        return followGraph.hasEdge(followerId, followingId);
    }

    // Get suggested users (friends of friends)
    public Set<User> getSuggestedUsers(Long userId, int maxSuggestions) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("User not found in graph");
        }

        Set<Long> suggestedIds = new HashSet<>();
        Set<Long> alreadyFollowing = followGraph.getFollowing(userId);

        // Add friends of friends
        for (Long followingId : alreadyFollowing) {
            suggestedIds.addAll(followGraph.getFollowing(followingId));
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

    // Get users for generating a feed (user + following)
    public Set<User> getFeedUsers(Long userId) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("User not found in graph");
        }

        Set<User> feedUsers = new HashSet<>();
        feedUsers.add(users.get(userId)); // Add the user themselves
        feedUsers.addAll(getFollowing(userId)); // Add all users they follow
        return feedUsers;
    }
}
