### **Graph Data Structure Tutorial with Implementation in Code**

A graph is a data structure consisting of:
- **Vertices (nodes):** Represent entities (e.g., users in a social network).
- **Edges:** Represent relationships or connections between the entities.

Graphs are widely used in social networks, recommendation systems, and pathfinding algorithms. Below is a comprehensive tutorial on graph data structures, including operations and their implementation in the provided code.

---

### **1. Graph Representation**
Graphs can be represented in multiple ways:
1. **Adjacency Matrix:** A 2D array where `matrix[i][j] = 1` indicates an edge from vertex `i` to `j`.
2. **Adjacency List:** A map where each vertex has a list of connected vertices.
3. **Edge List:** A list of all edges, where each edge is represented as a pair `(u, v)`.

The given code uses a database-backed **Adjacency List**:
- Each user is a vertex.
- The "follows" relationship is a directed edge from `follower` to `following`.

**Database Table: `follows`**
- `follower_id`: User initiating the follow.
- `following_id`: User being followed.

---

### **2. Basic Operations**

#### **2.1 Add a Vertex**
Adding a vertex means adding a new user to the graph.

In the code:
- A new user is registered using the **`AuthController`**.

```java
@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody User user) {
    authService.register(user);
    return ResponseEntity.ok().body(Map.of("message", "User registered successfully"));
}
```

The user is stored in the database and becomes a vertex in the graph.

---

#### **2.2 Add an Edge (Follow a User)**

Adding an edge creates a connection between two vertices:
- Directed edge from the follower to the following user.

In the code:
- **API Endpoint:** `POST /api/follow/{followingId}`.
- **Service Method:** `followUser`.

```java
public void followUser(Long followerId, Long followingId) {
    // Check for valid users
    validateUsers(followerId, followingId);

    // Add an edge in the database
    Follow follow = new Follow(followerId, followingId);
    followRepository.save(follow);
}
```

- **Steps:**
  1. Validate that both `follower` and `following` users exist.
  2. Add an entry in the `follows` table.

---

#### **2.3 Remove an Edge (Unfollow a User)**

Removing an edge disconnects two vertices:
- Deletes the directed edge from the follower to the following user.

In the code:
- **API Endpoint:** `DELETE /api/follow/{followingId}`.
- **Service Method:** `unfollowUser`.

```java
public void unfollowUser(Long followerId, Long followingId) {
    validateUsers(followerId, followingId);

    // Remove edge from the database
    followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
}
```

- **Steps:**
  1. Validate that the users exist.
  2. Remove the entry from the `follows` table.

---

#### **2.4 Get Neighbors (Followers/Following)**

Finding neighbors means retrieving connected nodes:
- **Outgoing edges:** Users the current user is following.
- **Incoming edges:** Users who follow the current user.

In the code:
- **API Endpoint:** `GET /api/follow/followers/{userId}` or `GET /api/follow/following/{userId}`.
- **Service Methods:** `getFollowersList`, `getFollowingList`.

```java
public List<User> getFollowersList(Long userId) {
    return followRepository.findFollowersByUserId(userId);
}

public List<User> getFollowingList(Long userId) {
    return followRepository.findFollowingByUserId(userId);
}
```

- **Steps:**
  1. Query the `follows` table for incoming (`follower_id`) or outgoing (`following_id`) edges.
  2. Return the list of connected users.

---

#### **2.5 Check Connectivity (Is Following)**

Checking connectivity determines if there is a direct edge between two vertices:
- Is `follower_id` connected to `following_id`?

In the code:
- **API Endpoint:** `GET /api/follow/check/{followerId}/{followingId}`.
- **Service Method:** `isFollowing`.

```java
public boolean isFollowing(Long followerId, Long followingId) {
    return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
}
```

---

### **3. Advanced Operations**

#### **3.1 Pathfinding**
Graphs can be used for advanced operations like finding paths or suggesting connections. In social networks:
- **Find mutual followers.**
- **Recommend new users to follow.**

**Recommendation Example:**
- Suggest users who are followed by people you follow but whom you don’t follow yet.
  
**Implementation (Pseudocode):**
```java
public List<User> getRecommendations(Long userId) {
    // Get users followed by the user's connections
    List<Long> friends = followRepository.findFollowingByUserId(userId);
    Set<Long> recommendations = new HashSet<>();

    for (Long friendId : friends) {
        List<Long> friendsOfFriend = followRepository.findFollowingByUserId(friendId);
        for (Long suggested : friendsOfFriend) {
            if (!friends.contains(suggested) && suggested != userId) {
                recommendations.add(suggested);
            }
        }
    }
    return userRepository.findUsersByIds(recommendations);
}
```

---

#### **3.2 Cache for Optimization**
The backend uses a **Binary Search Tree (BST)** as a cache to optimize user lookups. This ensures:
- Efficient insertion (`O(log n)`).
- Fast retrieval of users by username.

**Example of Cache Fetch:**
```java
public User getUserByUsername(String username) {
    TreeNode node = searchNodeForUsername(root, username);
    if (node != null) {
        return node.getUser();
    }
    return null; // Cache miss
}
```

---

### **4. Complexity Analysis**

1. **Follow/Unfollow:**
   - **Database:** `O(1)` for insert/delete operation.
   - **Cache Update:** `O(log n)` for BST insertion/removal.

2. **Fetch Followers/Following:**
   - **Database Query:** `O(k)`, where `k` is the number of edges.

3. **Recommendation:**
   - **Traversal:** `O(k * f)`, where `k` is the number of connections and `f` is the average number of friends.

---

### **5. Example Usage in the Application**

1. **Follow a User:**
   - User clicks the "Follow" button (`FollowButton.js`).
   - The button triggers an API call to `/api/follow/{followingId}`.
   - The backend adds the relationship in the database and updates the cache.

2. **View Followers:**
   - A user visits their profile (`UserProfile.js`).
   - The frontend calls `/api/follow/followers/{userId}`.
   - The backend retrieves the followers from the database.

---

### **6. Summary**

This application uses a graph data structure to represent and manage user relationships. Basic operations like follow, unfollow, and fetch followers/following are efficiently implemented using adjacency lists backed by a relational database. The BST cache further optimizes user lookups.

#### **Key Features:**
- Scalable representation using adjacency lists.
- Efficient cache for user retrieval.
- Flexible extensions for advanced features like recommendations.

This tutorial provides a strong foundation for understanding and building graph-based applications. Let me know if you’d like to implement additional features or optimizations!