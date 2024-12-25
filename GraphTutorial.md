### Documentation for the Follow/Unfollow Functionality

The provided code implements a follow/unfollow mechanism in a simulated social media application. This section describes the follow/unfollow functionality, focusing on how the graph data structure is used and managed.

---

#### **Overview of Follow/Unfollow**

1. **Follow:**
   - Allows one user to follow another.
   - Adds a directed edge in the social graph from the follower to the following user.
   - Updates the backend to reflect the new relationship.

2. **Unfollow:**
   - Removes the directed edge in the social graph.
   - Updates the backend to reflect this change.

3. **Graph Representation:**
   - Users are represented as nodes.
   - Follow relationships are represented as directed edges in the graph.

---

#### **Components Involved**

1. **Frontend Components:**
   - `FollowButton.js`: Handles the user interface for follow/unfollow actions.
   - `DiscoverUsers.js` and `UserProfile.js`: Allow users to find others and manage follow relationships.

2. **Backend Services:**
   - **SocialGraphService**:
     - Manages the graph structure.
     - Provides methods to add or remove edges (follow/unfollow).
   - **FollowController**:
     - API endpoints to interact with the social graph.

3. **Database:**
   - Stores the graph relationships (edges) and user information (nodes).

---

#### **Code Walkthrough**

##### **Frontend Follow/Unfollow Logic**

- **`FollowButton.js`**:
  - Sends a request to the backend to follow/unfollow a user.
  - Updates the local UI state (`isFollowing`) based on the backend response.

```javascript
const handleFollowClick = async () => {
    setLoading(true);
    try {
        if (isFollowing) {
            await unfollowUser(userId);
        } else {
            await followUser(userId);
        }
        setIsFollowing(!isFollowing);
        if (onFollowChange) {
            onFollowChange();
        }
    } catch (error) {
        console.error('Error updating follow status:', error);
    } finally {
        setLoading(false);
    }
};
```

---

##### **Backend Follow/Unfollow APIs**

1. **Follow User API:**
   - Adds a directed edge in the graph.

```java
@PostMapping("/{followingId}")
public ResponseEntity<?> followUser(HttpServletRequest request, @PathVariable Long followingId) {
    Long followerId = (Long) request.getAttribute("userId");
    socialGraphService.followUser(followerId, followingId);
    return ResponseEntity.ok(Map.of("message", "Successfully followed user"));
}
```

2. **Unfollow User API:**
   - Removes the directed edge.

```java
@DeleteMapping("/{followingId}")
public ResponseEntity<?> unfollowUser(HttpServletRequest request, @PathVariable Long followingId) {
    Long followerId = (Long) request.getAttribute("userId");
    socialGraphService.unfollowUser(followerId, followingId);
    return ResponseEntity.ok(Map.of("message", "Successfully unfollowed user"));
}
```

---

#### **Graph Data Structure**

The follow/unfollow functionality inherently uses a **directed graph** where:
- **Nodes:** Represent users.
- **Edges:** Represent the "follows" relationship (directed from follower to following).

##### **Graph Storage**
- Relationships are stored in a database, typically in a relational format:
  - **Table:** `follows`
    - `follower_id` → References the user who follows.
    - `following_id` → References the user being followed.

---

#### **How the Graph is Used**

1. **Follow User:**
   - Inserts a new directed edge from `follower` to `following`.

2. **Unfollow User:**
   - Deletes the directed edge.

3. **Fetching Followers/Following:**
   - Queries the graph to find incoming edges (followers) or outgoing edges (following).

```java
public List<User> getFollowersList(Long userId) {
    // Find all users who have an edge directed to this user
    return userRepository.findFollowers(userId);
}

public List<User> getFollowingList(Long userId) {
    // Find all users to whom this user has an outgoing edge
    return userRepository.findFollowing(userId);
}
```

---

#### **Advantages of Using Graphs**
1. **Efficiency:**
   - Fast lookups for followers and following relationships.
   - Efficient traversal for recommendations.

2. **Scalability:**
   - Easily scales with user base.

3. **Flexibility:**
   - Additional features like mutual followers or recommendations are straightforward to implement.

---

#### **Key API Endpoints**

1. `POST /api/follow/{followingId}` - Follow a user.
2. `DELETE /api/follow/{followingId}` - Unfollow a user.
3. `GET /api/follow/followers/{userId}` - Get followers of a user.
4. `GET /api/follow/following/{userId}` - Get users followed by a user.

---

