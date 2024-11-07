# Week 2: Tweet Microservice Implementation Pseudocode

## Frontend Components

### Tweet Model

```javascript
class Tweet {
  constructor(id, content, timestamp, username) {
    this.id = id;
    this.content = content;
    this.timestamp = timestamp;
    this.username = username;
  }
}
```

### Tweet Component

```jsx
function Tweet({ tweet }) {
  return (
    <div className="tweet">
      <div className="tweet-header">
        <span className="username">{tweet.username}</span>
        <span className="timestamp">{formatTimestamp(tweet.timestamp)}</span>
      </div>
      <div className="tweet-content">{tweet.content}</div>
    </div>
  );
}
```

### TweetForm Component

```jsx
function TweetForm() {
  const [content, setContent] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim()) {
      setError("Tweet cannot be empty");
      return;
    }
    try {
      await tweetService.postTweet(content);
      setContent("");
      setError("");
      // Trigger tweet list refresh
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        maxLength={280}
      />
      {error && <div className="error">{error}</div>}
      <button type="submit">Tweet</button>
    </form>
  );
}
```

### Tweet Service

```javascript
const tweetService = {
  async fetchTweets(page = 0, size = 10) {
    const response = await fetch(`/api/tweets?page=${page}&size=${size}`, {
      headers: {
        Authorization: `Bearer ${getToken()}`,
      },
    });
    if (!response.ok) throw new Error("Failed to fetch tweets");
    return response.json();
  },

  async postTweet(content) {
    const response = await fetch("/api/tweets", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${getToken()}`,
      },
      body: JSON.stringify({ content }),
    });
    if (!response.ok) throw new Error("Failed to post tweet");
    return response.json();
  },
};
```

### Main Dashboard Component

```jsx
function Dashboard() {
  const [tweets, setTweets] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    loadTweets();
  }, [page]);

  const loadTweets = async () => {
    try {
      const data = await tweetService.fetchTweets(page);
      setTweets(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      console.error("Failed to load tweets:", err);
    }
  };

  return (
    <div>
      <TweetForm onTweetPosted={loadTweets} />
      <div className="tweet-list">
        {tweets.map((tweet) => (
          <Tweet key={tweet.id} tweet={tweet} />
        ))}
      </div>
      <div className="pagination">
        <button onClick={() => setPage((p) => p - 1)} disabled={page === 0}>
          Previous
        </button>
        <span>
          Page {page + 1} of {totalPages}
        </span>
        <button
          onClick={() => setPage((p) => p + 1)}
          disabled={page >= totalPages - 1}
        >
          Next
        </button>
      </div>
    </div>
  );
}
```

## Backend Components

### Tweet Entity

```java
@Entity
@Table(name = "tweets")
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 280)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters, setters, constructors
}
```

### Tweet Repository

```java
@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    Page<Tweet> findAllByOrderByTimestampDesc(Pageable pageable);
    Page<Tweet> findByUserOrderByTimestampDesc(User user, Pageable pageable);
}
```

### Tweet Service

```java
@Service
public class TweetService {
    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    public Tweet createTweet(String content, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Tweet tweet = new Tweet();
        tweet.setContent(content);
        tweet.setTimestamp(LocalDateTime.now());
        tweet.setUser(user);

        return tweetRepository.save(tweet);
    }

    public Page<Tweet> getTweets(int page, int size) {
        return tweetRepository.findAllByOrderByTimestampDesc(
            PageRequest.of(page, size)
        );
    }
}
```

### Tweet Controller

```java
@RestController
@RequestMapping("/api/tweets")
public class TweetController {
    @Autowired
    private TweetService tweetService;

    @PostMapping
    public ResponseEntity<?> createTweet(
        @RequestBody TweetRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            Tweet tweet = tweetService.createTweet(
                request.getContent(),
                userDetails.getUsername()
            );
            return ResponseEntity.ok(tweet);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Page<Tweet>> getTweets(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(tweetService.getTweets(page, size));
    }
}
```

### Security Configuration Update

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .antMatchers("/api/tweets/**").authenticated()
            .anyRequest().authenticated();
        // ... rest of the configuration
    }
}
```
