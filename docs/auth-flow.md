# Authentication Flow Documentation

## Overview
This document details the implementation of authentication (login/logout) in our Twitter-like application. The system uses JWT (JSON Web Tokens) for authentication and maintains both client-side and server-side session management.

## Components

### 1. Frontend Implementation

#### Login Component (`frontend/src/Login.js`)
```javascript
// Key functionality:
- Handles user login form
- Makes POST request to /api/auth/login
- Stores JWT token and username in localStorage
- Redirects to dashboard on success
```

**Implementation Details:**
```javascript
const login = async (credentials) => {
  const response = await fetch("http://localhost:8081/api/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(credentials)
  });
  
  if (response.ok) {
    const data = await response.json();
    localStorage.setItem("token", data.token);
    localStorage.setItem("username", data.username);
    return data;
  }
  throw new Error("Login failed");
};
```

#### Logout Implementation (`frontend/src/App.js`)
```javascript
// Key functionality:
- Clears local storage first (token and username)
- Makes POST request to /api/auth/logout
- Always redirects to login page
- Handles errors gracefully
```

**Implementation Details:**
```javascript
const handleLogout = async () => {
  const token = localStorage.getItem("token");
  
  // Clear local storage first
  localStorage.removeItem("token");
  localStorage.removeItem("username");

  try {
    if (token) {
      await fetch("http://localhost:8081/api/auth/logout", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        }
      });
    }
  } catch (error) {
    console.error("Logout failed:", error);
  } finally {
    window.location.href = "/login";
  }
};
```

#### Protected Route Component (`frontend/src/App.js`)
```javascript
// Key functionality:
- Checks for valid token in localStorage
- Redirects to login if no token found
```

**Implementation Details:**
```javascript
const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem("token");
  if (!token) {
    return <Navigate to="/login" />;
  }
  return children;
};
```

### 2. Backend Implementation

#### Authentication Controller (`backend/src/main/java/com/auth/controller/AuthController.java`)
```java
// Key endpoints:
- POST /api/auth/login
- POST /api/auth/logout
- POST /api/auth/register
```

**Login Implementation:**
```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    String username = credentials.get("username");
    String password = credentials.get("password");
    
    Optional<User> userOpt = authService.authenticate(username, password);
    
    if (userOpt.isPresent()) {
        User user = userOpt.get();
        UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername(username)
            .password("")
            .authorities("USER")
            .build();

        String token = jwtUtil.generateToken(userDetails);
        sessionManager.createSession(username, token);

        return ResponseEntity.ok(Map.of(
            "token", token,
            "username", username,
            "userId", user.getId()
        ));
    }
    return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
}
```

**Logout Implementation:**
```java
@PostMapping("/logout")
public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
    try {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            String username = jwtUtil.extractUsername(jwt);
            
            if (username != null) {
                authService.logout(username);
                return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
            }
        }
        return ResponseEntity.ok().body(Map.of("message", "Logged out"));
    } catch (Exception e) {
        logger.error("Error during logout: {}", e.getMessage());
        return ResponseEntity.ok().body(Map.of("message", "Logged out"));
    }
}
```

#### Authentication Service (`backend/src/main/java/com/auth/service/AuthService.java`)
```java
// Key functionality:
- User authentication
- Password validation
- Session management
- Cache management
```

**Key Methods:**
```java
public Optional<User> authenticate(String username, String password) {
    // Check cache first
    User cachedUser = cacheService.getUserByUsername(username);
    if (cachedUser != null && passwordEncoder.matches(password, cachedUser.getPassword())) {
        return Optional.of(cachedUser);
    }

    // Check database if not in cache
    return userRepository.findByUsername(username)
        .filter(user -> passwordEncoder.matches(password, user.getPassword()));
}

public void logout(String username) {
    sessionManager.invalidateSession(username);
    cacheService.removeUserFromCache(username);
}
```

#### Session Manager (`backend/src/main/java/com/auth/service/SessionManager.java`)
```java
// Key functionality:
- Manages active user sessions
- Handles session creation and invalidation
```

**Implementation:**
```java
@Service
public class SessionManager {
    private final Map<String, String> sessions = new HashMap<>();

    public void createSession(String username, String token) {
        sessions.put(username, token);
    }

    public void invalidateSession(String username) {
        sessions.remove(username);
    }

    public boolean isSessionValid(String username, String token) {
        String storedToken = sessions.get(username);
        return storedToken != null && storedToken.equals(token);
    }
}
```

## Security Measures

1. **JWT Token Security**
   - Tokens are signed with a secret key
   - Tokens have an expiration time
   - Tokens are validated on each request

2. **Password Security**
   - Passwords are hashed using BCrypt
   - Original passwords are never stored
   - Failed login attempts are logged

3. **Session Security**
   - Server-side session tracking
   - Automatic session invalidation on logout
   - Cache cleanup on logout

## Error Handling

1. **Frontend**
   - Network errors are caught and handled
   - Invalid credentials show user-friendly messages
   - Expired sessions redirect to login
   - Failed logouts still clear local state

2. **Backend**
   - Invalid tokens return 401 Unauthorized
   - Server errors are logged
   - Logout always succeeds from user perspective
   - Cache and session inconsistencies are handled gracefully

## Best Practices Implemented

1. **Frontend**
   - Clear local storage before server requests
   - Handle all error cases
   - Always redirect after logout
   - Protected routes for authenticated content

2. **Backend**
   - Proper password hashing
   - Token validation
   - Session management
   - Cache management
   - Comprehensive error handling
   - Proper HTTP status codes
   - Logging for debugging

## Flow Diagrams

### Login Flow
```
User -> Login Form -> Frontend Auth -> Backend Auth -> JWT Generation -> Session Creation -> Success Response -> Store Token -> Redirect to Dashboard
```

### Logout Flow
```
User -> Logout Button -> Clear Local Storage -> Backend Logout -> Clear Session -> Clear Cache -> Success Response -> Redirect to Login
```
