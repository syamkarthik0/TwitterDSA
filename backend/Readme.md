### **Data Structure Used for Password Storage and Management**

### **1. Hash Map for User Storage and Retrieval**

- **Class:** `AuthService`

```java
private final Map<String, User> users = new HashMap<>();
```

**Usage:**

- This **HashMap** stores user data, including **encrypted passwords**, with the **username as the key** and the **User object (containing the encrypted password) as the value**.
- **Why HashMap?**
  - Provides **O(1)** time complexity for inserting and retrieving user data.
  - Efficient for quick lookups during authentication.

---

### **2. Password Encoding with BCrypt (Hashing)**

- **Class:** `AuthService` and `SecurityConfig`

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**Usage:**

- **BCrypt** is a **password hashing algorithm** used to store passwords securely.
- **Why Hashing?**
  - Passwords are never stored in plain text. Instead, a **cryptographic hash** is stored. Even if the database is compromised, attackers cannot easily retrieve the original password.

**BCrypt Characteristics:**

- **Salted:** Every time a password is hashed, a random salt is added, ensuring that the same password generates a different hash.
- **Adaptive:** The algorithm can be configured to run slower over time, making brute-force attacks more difficult.

---

### **3. Optional for Authentication Handling**

- **Class:** `AuthService`

```java
public Optional<User> authenticate(String username, String password) {
    User user = users.get(username);
    if (user != null && passwordEncoder.matches(password, user.getPassword())) {
        return Optional.of(user);
    }
    return Optional.empty();
}
```

**Usage:**

- **`Optional<User>`** ensures safe handling of user authentication results. If the user is not found or the password does not match, it returns an **empty Optional**, preventing `NullPointerException`.

---

### **4. String Data Structure for Passwords**

- **Class:** `User`

```java
private String password;
```

**Usage:**

- Passwords are initially received as **strings** (from the frontend). They are then **encoded** using the `BCryptPasswordEncoder` and stored securely in the HashMap.

---

### **Summary of Data Structures Used for Passwords**

| **Data Structure**   | **Usage**                                                                                 |
| -------------------- | ----------------------------------------------------------------------------------------- |
| **HashMap**          | Store users with their encrypted passwords. Provides **O(1)** lookups for authentication. |
| **BCrypt (Hashing)** | Securely store passwords with adaptive, salted hashes. Prevents plain-text storage.       |
| **Optional**         | Handle null cases gracefully during authentication to avoid exceptions.                   |
| **String**           | Passwords are initially received as strings before being hashed.                          |

---

### **Authentication Flow Using Data Structures**

1. **User Registration:**

   - The password received as a string is **hashed** using `BCryptPasswordEncoder`.
   - The user data is stored in a **HashMap** with the username as the key and the encrypted password as part of the value.

2. **User Login:**

   - When a user logs in, the **HashMap** is used to retrieve the `User` object in **O(1)** time.
   - The password entered is **compared** with the hashed password using the **BCrypt `matches`** method.

3. **Optional Handling:**
   - If the username is not found or the password is incorrect, an **empty Optional** is returned, ensuring safe error handling.

---

This design ensures:

1. **Fast lookups** with the HashMap.
2. **Secure password storage** with BCrypt.
3. **Safe handling** of null or invalid cases using Optional.


##################

Graph:
The follower-following relationship forms a Directed Graph
Users are nodes
Following relationships are directed edges
Implemented through the Follower entity where:
Each edge has a source (follower) and destination (following)
This allows for traversal of the social network
ArrayList:
Used in the User class for storing tweets:
java
CopyInsert
private List<Tweet> tweets = new ArrayList<>();
Provides dynamic array implementation for storing user's tweets
O(1) for adding new tweets
O(n) for searching
Queue/Feed Implementation:
The UserFeed essentially works like a Queue
New tweets are added to followers' feeds
Tweets are typically displayed in chronological order (FIFO principle)
Implemented using database tables but conceptually represents a queue structure


Graph Structure Implementation:
java
CopyInsert
// Follower.java - Represents an edge in the graph
public class Follower {
    @EmbeddedId
    private FollowerId id;  // Composite key for the edge

    @ManyToOne
    @MapsId("followerId")
    @JoinColumn(name = "follower_id")
    private User follower;  // Source node (who is following)

    @ManyToOne
    @MapsId("followingId")
    @JoinColumn(name = "following_id")
    private User following; // Destination node (who is being followed)

    private LocalDateTime followDate;
}
Graph Operations:
a) Adding an Edge (Following a user):

java
CopyInsert
@Transactional
public void followUser(Long followerId, Long followingId) {
    // Check if edge already exists
    if (followerRepository.isFollowing(followerId, followingId)) {
        throw new IllegalStateException("Already following this user");
    }

    // Create new edge
    Follower relationship = new Follower();
    relationship.setId(new FollowerId(followerId, followingId));
    relationship.setFollower(follower);
    relationship.setFollowing(following);
    relationship.setFollowDate(LocalDateTime.now());
    
    followerRepository.save(relationship);
}
b) Removing an Edge (Unfollowing):

java
CopyInsert
@Transactional
public void unfollowUser(Long followerId, Long followingId) {
    // Check if edge exists
    if (!followerRepository.isFollowing(followerId, followingId)) {
        throw new IllegalStateException("Not following this user");
    }
    
    // Remove edge
    FollowerId id = new FollowerId(followerId, followingId);
    followerRepository.deleteById(id);
}
c) Graph Traversal (Finding followers/following):

java
CopyInsert
// Get all users that a user is following (outgoing edges)
@Query("SELECT f.following FROM Follower f WHERE f.follower.id = ?1")
List<User> findFollowingByFollowerId(Long followerId);

// Get all followers of a user (incoming edges)
@Query("SELECT f.follower FROM Follower f WHERE f.following.id = ?1")
List<User> findFollowersByFollowingId(Long followingId);
Graph Usage in Feed Generation:
java
CopyInsert
@Transactional
public void addTweetToFeeds(Tweet tweet) {
    // Traverse the graph to find all followers
    List<User> followers = followService.getFollowers(tweet.getUser().getId());
    
    // Add tweet to each follower's feed
    for (User follower : followers) {
        UserFeed feedItem = new UserFeed();
        feedItem.setUser(follower);
        feedItem.setTweet(tweet);
        feedItem.setCreatedAt(LocalDateTime.now());
        userFeedRepository.save(feedItem);
    }
}
The directed graph is used to:

Maintain social relationships (who follows whom)
Generate user feeds by traversing followers
Check relationships (isFollowing)
Find all followers/following of a user
Key Graph Operations and Their Complexity:

Add Edge (follow): O(1)
Remove Edge (unfollow): O(1)
Check Edge (isFollowing): O(1)
Get All Followers/Following: O(E) where E is number of edges
Feed Generation: O(V) where V is number of followers
The graph is implemented using a relational database, where:

Vertices (nodes) = Users table
Edges = Followers table with source and destination user IDs
Edge Properties = Follow date and other metadata
