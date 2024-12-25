### Graph Data Structure in the Code

The provided code leverages the **graph data structure** to manage relationships between users in a simulated social network. Below are the examples from the code that showcase how a graph is represented and manipulated.

---

### **Key Concepts in the Code**

1. **Graph Representation**:
   - **Nodes (Vertices):** Users in the system.
   - **Edges:** Follow relationships between users (directed edges).
   - The edges are stored in the database, effectively forming an **adjacency list**.

2. **Operations**:
   - **Add Edge:** A user follows another user (creates a directed edge).
   - **Remove Edge:** A user unfollows another (removes the directed edge).
   - **Retrieve Neighbors:** Get the list of users a person follows or their followers.

---

### **Examples from the Code**

#### **1. Add Edge (Follow a User)**

When a user follows another, the code adds a directed edge in the graph.

- **Backend Implementation:**

The `followUser` method in the `FollowController` is responsible for adding an edge:
```java
@PostMapping("/{followingId}")
public ResponseEntity<?> followUser(HttpServletRequest request, @PathVariable Long followingId) {
    Long followerId = (Long) request.getAttribute("userId");
    socialGraphService.followUser(followerId, followingId);
    return ResponseEntity.ok(Map.of("message", "Successfully followed user"));
}
```

- **How It Works:**
  1. The `followerId` and `followingId` are retrieved.
  2. The `SocialGraphService` updates the graph by inserting a new edge into the database.

---

#### **2. Remove Edge (Unfollow a User)**

Unfollowing a user removes the directed edge in the graph.

- **Backend Implementation:**

The `unfollowUser` method in the `FollowController`:
```java
@DeleteMapping("/{followingId}")
public ResponseEntity<?> unfollowUser(HttpServletRequest request, @PathVariable Long followingId) {
    Long followerId = (Long) request.getAttribute("userId");
    socialGraphService.unfollowUser(followerId, followingId);
    return ResponseEntity.ok(Map.of("message", "Successfully unfollowed user"));
}
```

- **How It Works:**
  1. Identifies the `followerId` and `followingId`.
  2. Removes the directed edge in the database, representing the graph.

---

#### **3. Retrieve Neighbors (Followers or Following)**

Fetching neighbors (followers or following users) is equivalent to retrieving all edges directed to or from a node.

- **Backend Implementation:**

The `getFollowers` and `getFollowing` methods in the `FollowController`:
```java
@GetMapping("/followers/{userId}")
public ResponseEntity<List<User>> getFollowers(@PathVariable Long userId) {
    return ResponseEntity.ok(socialGraphService.getFollowersList(userId));
}

@GetMapping("/following/{userId}")
public ResponseEntity<List<User>> getFollowing(@PathVariable Long userId) {
    return ResponseEntity.ok(socialGraphService.getFollowingList(userId));
}
```

- **How It Works:**
  1. Queries the database for incoming edges (followers) or outgoing edges (following).
  2. Returns the list of users corresponding to those edges.

---

#### **4. Check Connectivity (Is Following)**

Determine if a user is following another, which translates to checking for the existence of a directed edge between two nodes.

- **Backend Implementation:**
```java
@GetMapping("/check/{userId}/{followingId}")
public ResponseEntity<Boolean> isFollowing(@PathVariable Long userId, @PathVariable Long followingId) {
    return ResponseEntity.ok(socialGraphService.isFollowing(userId, followingId));
}
```

- **How It Works:**
  1. The service checks if there is an edge from `userId` to `followingId`.
  2. Returns `true` if an edge exists, `false` otherwise.

---

### **Frontend Integration**

The frontend interacts with the graph-based system via API calls.

1. **Follow/Unfollow Button**:
   - **File:** `FollowButton.js`
   - Dynamically updates the UI based on the user's follow status.

```javascript
const handleFollowClick = async () => {
    if (isFollowing) {
        await unfollowUser(userId);
    } else {
        await followUser(userId);
    }
    setIsFollowing(!isFollowing);
    if (onFollowChange) {
        onFollowChange();
    }
};
```

---

### **Visual Representation**

Let’s represent the graph operations with a simple diagram.

#### **Graph Before Adding Edge**
```
A → B
C
```

#### **Adding an Edge: A Follows C**
```
A → B
↓
C
```

#### **Removing an Edge: A Unfollows B**
```
A
↓
C
```

#### **Fetching Neighbors**
- **Followers of C:** `A`
- **Following of A:** `C`

---

### **Conclusion**

The code effectively uses a graph data structure to manage social connections. The graph is stored in the database and manipulated through APIs for operations like adding/removing edges and retrieving neighbors. The frontend ensures users can interact seamlessly with the graph, updating their social connections dynamically.