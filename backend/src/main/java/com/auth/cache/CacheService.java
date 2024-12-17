package com.auth.cache;

import com.auth.model.User;
import com.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.Comparator;
import java.util.Optional;

@Service
public class CacheService {

    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);
    private static final int MAX_CACHE_SIZE = 10000; // Maximum cache size
    private TreeNode root; // Root of the binary search tree
    private int size = 0;
    private Map<String, Long> lastAccessTime = new HashMap<>();
    private Map<String, Integer> cacheHits = new HashMap<>();
    private long totalRequests = 0;
    private long cacheHitCount = 0;
    private long cacheMissCount = 0;

    @Autowired
    private UserRepository userRepository; // Inject UserRepository for database interaction

    // Check if the cache is empty
    public boolean isCacheEmpty() {
        logger.info("Checking if cache is empty...");
        return root == null;
    }

    // Add a user to the cache
    public void addUserToCache(User user) {
        logger.info("Adding user to cache: {}", user.getUsername());
        if (size >= MAX_CACHE_SIZE) {
            evictLeastRecentlyUsed();
        }
        root = insertRec(root, user);
        lastAccessTime.put(user.getUsername(), System.currentTimeMillis());
        size++;
    }

    // Recursive method to insert a user into the BST
    private TreeNode insertRec(TreeNode root, User user) {
        if (root == null) {
            logger.debug("Inserting user at a new tree node: {}", user.getUsername());
            return new TreeNode(user);
        }

        String username = user.getUsername();
        if (username.compareTo(root.getUser().getUsername()) < 0) {
            root.setLeft(insertRec(root.getLeft(), user));
        } else {
            root.setRight(insertRec(root.getRight(), user));
        }

        return root;
    }

    // Fetch a user by username
    public User getUserByUsername(String username) {
        logger.info("Fetching user for username: {}", username);
        totalRequests++;
        
        TreeNode node = searchNodeForUsername(root, username);
        if (node == null) {
            logger.warn("Cache MISS for username: {}", username);
            cacheMissCount++;
            return null;
        }

        logger.info("Cache HIT for username: {}", username);
        cacheHitCount++;
        cacheHits.merge(username, 1, Integer::sum);
        lastAccessTime.put(username, System.currentTimeMillis());
        return node.getUser();
    }

    // Fetch all users
    public List<User> getAllUsersFromCache() {
        logger.info("Fetching all users from cache...");
        List<User> users = new ArrayList<>();
        traverseTree(root, users);
        return users;
    }

    // Search for a node by username
    private TreeNode searchNodeForUsername(TreeNode root, String username) {
        if (root == null) {
            return null;
        }

        String currentUsername = root.getUser().getUsername();
        if (currentUsername.equals(username)) {
            return root;
        }

        if (username.compareTo(currentUsername) < 0) {
            return searchNodeForUsername(root.getLeft(), username);
        }

        return searchNodeForUsername(root.getRight(), username);
    }

    // Traverse the tree and collect users
    private void traverseTree(TreeNode node, List<User> users) {
        if (node != null) {
            traverseTree(node.getLeft(), users);
            users.add(node.getUser());
            traverseTree(node.getRight(), users);
        }
    }

    // Evict the least recently used user from the cache
    private void evictLeastRecentlyUsed() {
        String lruUser = lastAccessTime.entrySet()
            .stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
        
        if (lruUser != null) {
            logger.info("Evicting least recently used user: {}", lruUser);
            removeFromCache(lruUser);
            lastAccessTime.remove(lruUser);
            size--;
        }
    }

    // Remove a user from the cache
    private void removeFromCache(String username) {
        root = removeRec(root, username);
    }

    // Recursive method to remove a user from the BST
    private TreeNode removeRec(TreeNode root, String username) {
        if (root == null) {
            return null;
        }

        int comparison = username.compareTo(root.getUser().getUsername());
        if (comparison < 0) {
            root.setLeft(removeRec(root.getLeft(), username));
        } else if (comparison > 0) {
            root.setRight(removeRec(root.getRight(), username));
        } else {
            // Node to delete found
            if (root.getLeft() == null) {
                return root.getRight();
            } else if (root.getRight() == null) {
                return root.getLeft();
            }
            
            // Node with two children
            TreeNode successor = findMin(root.getRight());
            root.setUser(successor.getUser());
            root.setRight(removeRec(root.getRight(), successor.getUser().getUsername()));
        }
        return root;
    }

    // Find the node with the minimum value in the tree
    private TreeNode findMin(TreeNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    // Get cache statistics
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("size", size);
        stats.put("maxSize", MAX_CACHE_SIZE);
        stats.put("totalRequests", totalRequests);
        stats.put("cacheHits", cacheHitCount);
        stats.put("cacheMisses", cacheMissCount);
        stats.put("hitRate", totalRequests > 0 ? (double) cacheHitCount / totalRequests : 0);
        stats.put("mostAccessedUsers", getMostAccessedUsers(5));
        return stats;
    }

    private List<Map<String, Object>> getMostAccessedUsers(int limit) {
        return cacheHits.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                Map<String, Object> userStats = new HashMap<>();
                userStats.put("username", entry.getKey());
                userStats.put("hits", entry.getValue());
                userStats.put("lastAccessed", lastAccessTime.get(entry.getKey()));
                return userStats;
            })
            .collect(Collectors.toList());
    }

    // Scheduled method to refresh the cache
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void refreshCache() {
        logger.info("Starting incremental cache refresh...");
        List<User> dbUsers = userRepository.findAll();
        Set<String> dbUsernames = dbUsers.stream()
            .map(User::getUsername)
            .collect(Collectors.toSet());
        
        // Remove users that no longer exist in DB
        List<String> cachedUsers = getAllUsersFromCache()
            .stream()
            .map(User::getUsername)
            .collect(Collectors.toList());
            
        for (String username : cachedUsers) {
            if (!dbUsernames.contains(username)) {
                removeFromCache(username);
                lastAccessTime.remove(username);
                size--;
                logger.info("Removed deleted user from cache: {}", username);
            }
        }
        
        // Update or add new users
        for (User user : dbUsers) {
            TreeNode node = searchNodeForUsername(root, user.getUsername());
            if (node == null || !node.getUser().equals(user)) {
                addUserToCache(user);
                logger.info("Updated/Added user in cache: {}", user.getUsername());
            }
        }
        logger.info("Incremental cache refresh completed. Current cache size: {}", size);
    }
}
