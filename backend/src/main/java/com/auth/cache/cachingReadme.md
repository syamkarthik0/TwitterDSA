Cache Implementation Overview
The caching system uses a Binary Search Tree (BST) data structure combined with a Least Recently Used (LRU) eviction policy. Here's a detailed breakdown:

Data Structure
Uses a Binary Search Tree (BST) for storing and retrieving user data
The BST is implemented using the TreeNode class which contains:
User data
Left child reference
Right child reference
Cache Properties
Maximum cache size: 10,000 entries
Uses LRU (Least Recently Used) eviction policy
Maintains statistics like hit rate, cache hits, and cache misses
Key Operations
a) Insertion (addUserToCache)

java
CopyInsert
- Checks if cache is at maximum capacity
- If full, evicts least recently used entry
- Inserts new user into BST using username as key
- Updates last access time
- Increments cache size
b) Retrieval (getUserByUsername)

java
CopyInsert
- Searches BST for username
- If found: 
  - Updates access time
  - Increments hit counter
  - Returns user
- If not found:
  - Increments miss counter
  - Returns null
c) Eviction (evictLeastRecentlyUsed)

java
CopyInsert
- Finds user with oldest access timestamp
- Removes user from BST
- Updates cache size
- Removes access time entry
Cache Maintenance
Automatic refresh every 5 minutes
Synchronizes cache with database
Removes deleted users
Updates modified users
Adds new users
Performance Monitoring
Tracks total requests
Monitors cache hits and misses
Calculates hit rate
Tracks most accessed users
Monitors cache size
BST Implementation Details
Tree Operations
a) Search

java
CopyInsert
- Recursive search based on username comparison
- O(log n) average case time complexity
- Returns null if username not found
b) Insert

java
CopyInsert
- Recursive insertion maintaining BST property
- Compares usernames to determine left/right placement
- Creates new node if position found
c) Delete

java
CopyInsert
- Handles three cases:
  1. Node with no children
  2. Node with one child
  3. Node with two children (uses successor)
- Maintains BST properties after deletion
Tree Traversal
Uses in-order traversal for retrieving all users
Maintains sorted order based on username
Cache Statistics and Monitoring
The cache provides real-time statistics through the getCacheStats() method:

Current cache size
Maximum cache size
Total requests
Cache hits and misses
Hit rate
Most accessed users (top 5)
Last access times
This implementation provides efficient O(log n) operations for most operations while maintaining a balance between memory usage and performance through the LRU eviction policy.