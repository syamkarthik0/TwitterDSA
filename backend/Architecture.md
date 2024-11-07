# Week 2: Tweet Microservice Architecture

## System Overview

The Tweet microservice extends the Week 1 authentication system by adding tweet posting and retrieval capabilities. The system maintains a clean separation of concerns between frontend and backend components while ensuring secure communication through JWT authentication.

## Architecture Components

### Frontend Architecture

#### Components Hierarchy

```
App
├── Login
├── Register
└── Dashboard
    ├── TweetForm
    └── TweetList
        └── Tweet
```

#### State Management

- Uses React's useState for local component state
- JWT token stored in localStorage for authentication
- Tweet data managed at Dashboard level and passed down to child components

#### Data Flow

1. User composes tweet in TweetForm
2. Form submission triggers API call through tweetService
3. On successful post, Dashboard refreshes tweet list
4. Pagination controls trigger new data fetches
5. Tweet components receive data as props and render

### Backend Architecture

#### Component Layers

1. **Controller Layer** (REST API endpoints)

   - TweetController: Handles HTTP requests
   - Input validation
   - Authentication verification
   - Response formatting

2. **Service Layer** (Business Logic)

   - TweetService: Core business logic
   - User association
   - Data validation
   - Transaction management

3. **Repository Layer** (Data Access)

   - TweetRepository: Database operations
   - Pagination handling
   - Sorting implementation

4. **Model Layer** (Data Entities)
   - Tweet: Database entity
   - Relationships with User entity

#### Database Schema

```sql
CREATE TABLE tweets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(280) NOT NULL,
    timestamp DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Indexes
CREATE INDEX idx_tweets_timestamp ON tweets(timestamp DESC);
CREATE INDEX idx_tweets_user_id ON tweets(user_id);
```

## Security Architecture

### Authentication Flow

1. JWT token required for all tweet operations
2. Token validated through JwtRequestFilter
3. User identity extracted from token
4. Authorization checks performed at endpoint level

### Security Measures

- CORS configuration for frontend access
- Request rate limiting
- Input validation and sanitization
- SQL injection prevention through JPA
- XSS protection

## API Endpoints

### Tweet Management

```
POST /api/tweets
- Creates new tweet
- Requires authenticated user
- Request body: { "content": "string" }
- Returns: Tweet object

GET /api/tweets?page={page}&size={size}
- Retrieves paginated tweets
- Requires authenticated user
- Query params: page (default 0), size (default 10)
- Returns: Page<Tweet> object
```

## Data Structures and Algorithms

### Database Indexing

- B-tree index on timestamp for efficient sorting
- B-tree index on user_id for relationship queries

### Pagination Implementation

- Offset-based pagination using Spring Data
- Configurable page size
- Sort by timestamp in descending order

## Performance Considerations

### Database Optimization

- Indexed queries for efficient retrieval
- Lazy loading of user relationships
- Connection pooling

### Caching Strategy

- Consider implementing Redis for:
  - Frequently accessed tweets
  - User data caching
  - Rate limiting implementation

### Frontend Optimization

- Pagination to limit data transfer
- Debounced API calls
- Optimistic UI updates

## Error Handling

### Frontend Error Handling

- Form validation errors
- API error responses
- Network error handling
- Loading states

### Backend Error Handling

- Global exception handler
- Custom error responses
- Validation error handling
- Database error handling

## Testing Strategy

### Frontend Testing

- Component unit tests
- Integration tests
- E2E tests with Cypress

### Backend Testing

- Unit tests for services
- Integration tests for controllers
- Repository layer tests
- Security tests

## Future Enhancements

### Potential Features

1. Tweet deletion
2. Tweet editing
3. User mentions
4. Hashtag support
5. Media attachments

### Scalability Considerations

1. Implement caching layer
2. Message queue for async operations
3. CDN for media content
4. Database sharding strategy
