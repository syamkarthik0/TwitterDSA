## Pseudo Code for Java Files

### **`AuthApplication.java`**

```java
// Define package for the class
package com.auth;

// Import necessary Spring Boot libraries for application setup
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Declare the main application class, annotated as a Spring Boot Application
@SpringBootApplication
public class AuthApplication {

    // Main method to start the Spring Boot application
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
```

### **`AuthController.java`**

```java
// Define package for the class
package com.auth.controller;

// Import necessary classes for handling HTTP requests, responses,
// user authentication, JWT token generation, and session management.
import com.auth.model.User;
import com.auth.security.JwtUtil;
import com.auth.service.AuthService;
import com.auth.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Declare the controller class, handling requests related to authentication
@RestController
@RequestMapping("/api/auth")
// Allow requests from any origin
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    // Inject instances of services and utilities required for authentication
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SessionManager sessionManager;

    // Handle user registration requests
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) {
        try {
            // Attempt to register the user using the AuthService
            User registeredUser = authService.register(user);
            // Create a UserDetails object with the registered user's information
            UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername(registeredUser.getUsername())
            .password("")
            .authorities(registeredUser.getRole())
            .build();
            // Generate a JWT token for the user
            String token = jwtUtil.generateToken(userDetails);
            // Create a session for the user
            sessionManager.createSession(registeredUser.getUsername(), token);
            // Prepare a response with success message, token, username, and role
            Map response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("token", token);
            response.put("username", registeredUser.getUsername());
            response.put("role", registeredUser.getRole());
            // Return the response with OK status
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Handle registration error and return a Bad Request response with the error message
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Handle user login requests
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map credentials) {
        // Check if both username and password are provided
        if (!credentials.containsKey("username") || !credentials.containsKey("password")) {
            // Return a Bad Request response if credentials are missing
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
        }
        // Extract username and password from the request
        String username = credentials.get("username");
        String password = credentials.get("password");
        try {
            // Attempt to authenticate the user using the AuthService
            Optional userOpt = authService.authenticate(username, password);
            // If authentication is successful
            if (userOpt.isPresent()) {
                // Get the authenticated user
                User user = userOpt.get();
                // Create a UserDetails object with the authenticated user's information
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("")
                .authorities(user.getRole())
                .build();
                // Generate a JWT token for the user
                String token = jwtUtil.generateToken(userDetails);
                // Create a session for the user
                sessionManager.createSession(username, token);
                // Prepare a response with the token, username, and role
                Map response = new HashMap<>();
                response.put("token", token);
                response.put("username", username);
                response.put("role", user.getRole());
                // Return the response with OK status
                return ResponseEntity.ok(response);
            }
            // If authentication fails, return a Bad Request response
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            // Handle authentication error and return a Bad Request response with the error message
            return ResponseEntity.badRequest().body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }

    // Handle user logout requests
    @PostMapping("/logout")
    public ResponseEntity logout(@RequestHeader(value = "Authorization", required = false) String token) {
        // Check if the token is present and starts with "Bearer "
        if (token == null || !token.startsWith("Bearer ")) {
            // Return a Bad Request response if the token is missing or invalid
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or missing token"));
        }
        try {
            // Extract the JWT from the Authorization header
            String jwt = token.substring(7);
            // Extract the username from the JWT
            String username = jwtUtil.extractUsername(jwt);
            // Create a UserDetails object for validation
            UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername(username)
            .password("")
            .authorities("USER")
            .build();
            // Validate the JWT token
            if (!jwtUtil.validateToken(jwt, userDetails)) {
                // Return a Bad Request response if the token is invalid or expired
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired token"));
            }
            // Logout the user using the AuthService
            authService.logout(username);
            // Return an OK response with a logout success message
            return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            // Handle logout error and return a Bad Request response with the error message
            return ResponseEntity.badRequest().body(Map.of("error", "Logout failed: " + e.getMessage()));
        }
    }
}
```

### **`User.java`**

```java
// Define the package for the class
package com.auth.model;

// Import necessary Lombok annotations for generating boilerplate code
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Import JPA annotations for mapping the class to a database table
import javax.persistence.*;

// Annotate the class with @Data to automatically generate getters, setters,
// equals, hashCode, and toString methods.
@Data

// Annotate the class with @NoArgsConstructor to generate a constructor
// with no arguments.
@NoArgsConstructor

// Annotate the class with @AllArgsConstructor to generate a constructor
// with all arguments.
@AllArgsConstructor

// Annotate the class with @Entity to mark it as a JPA entity
@Entity

// Annotate the class with @Table to specify the table name in the database
@Table(name = "users")
public class User {

    // Define the 'id' field as the primary key,
    // auto-generated and incremented by the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Define the 'username' field, ensuring it's unique and not nullable
    @Column(unique = true, nullable = false)
    private String username;

    // Define the 'password' field, ensuring it's not nullable
    @Column(nullable = false)
    private String password;

    // Define the 'role' field with a default value of "USER"
    @Column(nullable = false)
    private String role = "USER";
}
```

### **`UserRepository.java`**

```java
// Define the package for the interface
package com.auth.repository;

// Import necessary classes for interacting with the User model
import com.auth.model.User;

// Import Spring Data JPA annotations and interfaces for database operations
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Import Java utility for working with optional values
import java.util.Optional;

// Annotate the interface with @Repository to mark it as a Spring repository
@Repository
// Define the UserRepository interface, extending JpaRepository for basic CRUD operations
public interface UserRepository extends JpaRepository<User, Long> {

    // Define a method to find a User by their username, returning an optional User
    Optional<User> findByUsername(String username);

    // Define a method to check if a User exists with a given username, returning a boolean
    boolean existsByUsername(String username);
}
```

### **`JwtRequestFilter.java`**

```java
// Define the package for the class
package com.auth.security;

// Import necessary classes for handling JWT tokens, session management,
// and Spring Security authentication.
import com.auth.service.SessionManager;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

// Annotate the class with @Component to mark it as a Spring bean
@Component
// Define the JwtRequestFilter class, extending OncePerRequestFilter
// to intercept and process JWT tokens from incoming requests
public class JwtRequestFilter extends OncePerRequestFilter {

    // Inject instances of the JwtUtil and SessionManager
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SessionManager sessionManager;

    // Override the doFilterInternal method to perform the filtering logic
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
    throws ServletException, IOException {
        // Get the Authorization header from the request
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // Check if the Authorization header is present and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the JWT token from the Authorization header
            jwt = authorizationHeader.substring(7);
            try {
                // Extract the username from the JWT token
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                // Log a warning if the JWT token is expired
                logger.warn("JWT Token has expired");
            }
        }

        // If the username is not null and the user is not authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Check if the session is valid using the SessionManager
            if (sessionManager.isSessionValid(username, jwt)) {
                // Create a UserDetails object for the user
                UserDetails userDetails = new User(username, "", new ArrayList<>());
                // Validate the JWT token using the JwtUtil
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // Create a UsernamePasswordAuthenticationToken
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                    // Set authentication details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Set the authentication in the SecurityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // Continue the filter chain
        chain.doFilter(request, response);
    }
}
```

### **`JwtUtil.java`**

```java
// Define the package for the class
package com.auth.security;

// Import necessary classes for working with JWT tokens and Spring Security UserDetails
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// Annotate the class with @Component to mark it as a Spring bean
@Component
// Define the JwtUtil class for generating and validating JWT tokens
public class JwtUtil {

    // Secret key for signing JWT tokens
    private String SECRET_KEY = "your_secret_key_here";
    // Token validity period in seconds
    private int TOKEN_VALIDITY = 3600 * 5; // 5 hours

    // Extract the username from the JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract the expiration date from the JWT token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract a specific claim from the JWT token using a provided claims resolver function
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the JWT token
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    // Check if the JWT token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Generate a JWT token for the provided UserDetails
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    // Create a JWT token with the provided claims and subject (username)
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
        .compact();
    }

    // Validate the JWT token against the provided UserDetails
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
```

### **`SecurityConfig.java`**

```java
// Define the package for the class
package com.auth.security;

// Import necessary Spring Security classes for configuration, password encoding, and CORS handling.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

// Annotate the class with @Configuration to mark it as a Spring configuration class
@Configuration

// Annotate the class with @EnableWebSecurity to enable Spring Security
@EnableWebSecurity
// Define the SecurityConfig class, extending WebSecurityConfigurerAdapter to customize security settings
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // Inject the JwtRequestFilter
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    // Override the configure method to define security rules and filters
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Enable CORS, disable CSRF protection
        http.cors().and().csrf().disable()
        // Configure authorization rules
        .authorizeRequests()
        // Allow public access to authentication endpoints
        .antMatchers("/api/auth/**").permitAll()
        // Allow access to H2 console
        .antMatchers("/h2-console/**").permitAll()
        // Require authentication for all other requests
        .anyRequest().authenticated()
        .and()
        // Disable frame options for H2 console
        .headers().frameOptions().disable()
        .and()
        // Configure session management to be stateless
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // Add the JWT filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    // Create a bean for the BCryptPasswordEncoder for password hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Create a bean for configuring CORS settings
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow requests from the React frontend running on localhost:3000
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        // Allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // Allow sending credentials
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### **`AuthService.java`**

```java
// Define the package for the class
package com.auth.service;

// Import necessary classes for user management, password encoding, and session management
import com.auth.model.User;
import com.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

// Annotate the class with @Service to mark it as a Spring service
@Service
// Define the AuthService class for handling user registration, authentication, and logout
public class AuthService {

    // Inject the UserRepository, PasswordEncoder, and SessionManager
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SessionManager sessionManager;

    // Register a new user
    public User register(User user) {
        // Check if the username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        // Set default role if not specified
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        // Encrypt password before storing
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Save the user to the database
        return userRepository.save(user);
    }

    // Authenticate a user with username and password
    public Optional<User> authenticate(String username, String password) {
        // Find the user by username
        Optional<User> userOpt = userRepository.findByUsername(username);
        // Check if the user exists and the password matches
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    // Logout a user by invalidating their session
    public void logout(String username) {
        sessionManager.invalidateSession(username);
    }
}
```

### **`SessionManager.java`**

```java
// Define the package for the class
package com.auth.service;

// Import necessary classes for managing user sessions
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

// Annotate the class with @Component to mark it as a Spring bean
@Component
// Define the SessionManager class for handling user sessions
public class SessionManager {

    // Map to store user sessions, keyed by username
    private final Map<String, String> userSessions = new HashMap<>();

    // Create a new session for a user
    public void createSession(String username, String token) {
        userSessions.put(username, token);
    }

    // Invalidate a user's session
    public void invalidateSession(String username) {
        userSessions.remove(username);
    }

    // Check if a session is valid for a given username and token
    public boolean isSessionValid(String username, String token) {
        String storedToken = userSessions.get(username);
        return storedToken != null && storedToken.equals(token);
    }
}
```
