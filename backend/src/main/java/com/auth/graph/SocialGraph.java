package com.auth.graph;

import com.auth.model.User;
import com.auth.graph.core.*;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
public class SocialGraph {
    private final DirectedGraph<User> socialGraph;

    public SocialGraph() {
        this.socialGraph = new DirectedGraph<>();
    }

    /**
     * Adds a user to the social graph.
     */
    public synchronized void addUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User or user ID cannot be null");
        }
        socialGraph.addNode(new Node<>(user.getId(), user));
    }

    /**
     * Creates a following relationship from follower to following user.
     */
    public synchronized void addFollowing(Long followerId, Long followingId) {
        if (!socialGraph.getNode(followerId).isPresent()) {
            throw new IllegalArgumentException("Follower not found in graph");
        }
        if (!socialGraph.getNode(followingId).isPresent()) {
            throw new IllegalArgumentException("Following user not found in graph");
        }
        socialGraph.addEdge(followerId, followingId);
    }

    /**
     * Removes a following relationship from follower to following user.
     */
    public synchronized void removeFollowing(Long followerId, Long followingId) {
        if (!socialGraph.getNode(followerId).isPresent()) {
            throw new IllegalArgumentException("Follower not found in graph");
        }
        if (!socialGraph.getNode(followingId).isPresent()) {
            throw new IllegalArgumentException("Following user not found in graph");
        }
        socialGraph.removeEdge(followerId, followingId);
    }

    /**
     * Gets all followers of a user.
     */
    public Set<User> getFollowers(Long userId) {
        return socialGraph.getIncomingNodes(userId).stream()
            .map(Node::getData)
            .collect(Collectors.toSet());
    }

    /**
     * Gets all users that a user is following.
     */
    public Set<User> getFollowing(Long userId) {
        return socialGraph.getOutgoingNodes(userId).stream()
            .map(Node::getData)
            .collect(Collectors.toSet());
    }

    /**
     * Checks if one user follows another.
     */
    public boolean isFollowing(Long followerId, Long followingId) {
        return socialGraph.hasEdge(followerId, followingId);
    }

    /**
     * Gets suggested users based on friends of friends.
     */
    public Set<User> getSuggestedUsers(Long userId, int maxSuggestions) {
        return socialGraph.getTwoHopNodes(userId).stream()
            .map(Node::getData)
            .limit(maxSuggestions)
            .collect(Collectors.toSet());
    }

    /**
     * Gets mutual connections between two users.
     */
    public Set<User> getMutualConnections(Long user1Id, Long user2Id) {
        return socialGraph.getMutualConnections(user1Id, user2Id).stream()
            .map(Node::getData)
            .collect(Collectors.toSet());
    }
}
