## Files, Pseudocode, Architecture and Flow Diagrams for Authentication System

Based on the provided code, here's a breakdown of the filenames, pseudocode for key operations, a potential architecture diagram, and a flow diagram for user authentication.

### Filenames

- `AuthApplication.java`
- `AuthController.java`
- `User.java`
- `UserRepository.java`
- `JwtRequestFilter.java`
- `JwtUtil.java`
- `SecurityConfig.java`
- `AuthService.java`
- `SessionManager.java`

### Pseudocode

**User Registration**

1.  Check if username already exists. If it does, throw an error.
2.  If no role is specified, set the user's role to "USER".
3.  Encrypt the user's password using `passwordEncoder`.
4.  Save the user to the database using `userRepository`.
5.  Generate a JWT (JSON Web Token) for the user using `jwtUtil`.
6.  Create a session for the user in `sessionManager`, associating the username with the generated token.
7.  Return a success response with the token, username, and role.

**User Login**

1.  Check if the request contains both username and password. If not, return an error.
2.  Try to authenticate the user using `authService`.
    - Retrieve the user from the database using `userRepository`.
    - Compare the provided password with the stored, encrypted password using `passwordEncoder`.
    - If authentication is successful, return the user details; otherwise, return an empty Optional.
3.  If authentication is successful:
    - Generate a JWT for the user.
    - Create a session in `sessionManager`.
    - Return a success response with the token, username, and role.
4.  If authentication fails, return an error message.

**User Logout**

1.  Check if the request contains a valid authorization header with a Bearer token. If not, return an error.
2.  Extract the JWT from the authorization header.
3.  Extract the username from the JWT using `jwtUtil`.
4.  Validate the JWT using `jwtUtil`.
    - Check if the token has expired.
    - Check if the username in the token matches the extracted username.
5.  If the token is valid:
    - Invalidate the user's session in `sessionManager`.
    - Return a success message.
6.  If the token is invalid, return an error message.

### Architecture Diagram

```
                                                     +----------------+
                                                     |                |
                                                     |  React Frontend |
                                                     |                |
                                                     +--------+--------+
                                                              |
                                                              | HTTP Requests
                                                              |
                                                     +--------v--------+
                                                     |                |
                                                     |  AuthController  |
                                                     |                |
                                                     +--------+--------+
                                                              |
                                                              | Calls
                                                              |
                                           +-----------------+-----------------+
                                           |                 |                 |
                                           |   AuthService     |  SessionManager  |
                                           |                 |                 |
                                           +--------+--------+--------+--------+
                                                    |                 |
                                                    | Uses            | Uses
                                                    v                 v
                                           +--------+--------+--------+--------+
                                           |                |                |
                                           |   JwtUtil       | UserRepository |
                                           |                |                |
                                           +-----------------+-----------------+
                                                    |
                                                    | Connects to
                                                    v
                                           +-----------------+
                                           |                |
                                           |    Database     |
                                           |                |
                                           +-----------------+

```

This diagram illustrates a simplified architecture where:

- The React Frontend interacts with the backend through HTTP requests.
- The `AuthController` handles authentication-related endpoints.
- `AuthService` manages user registration, login, and logout logic.
- `SessionManager` handles user sessions and token validation.
- `JwtUtil` is responsible for JWT generation and validation.
- `UserRepository` interacts with the database to manage user data.

### Flow Diagram for User Authentication

```mermaid
graph LR
A[User Login Request] --> B{AuthController}
B --> C[AuthService.authenticate()]
C --> D{UserRepository.findByUsername()}
D --> E{PasswordEncoder.matches()}
E -- Success --> F{JwtUtil.generateToken()}
F --> G{SessionManager.createSession()}
G --> H[Return Token]
E -- Failure --> I[Return Error]
```

This flow diagram focuses on the user login process:

1.  The user submits a login request.
2.  The `AuthController` delegates authentication to `AuthService`.
3.  `AuthService` retrieves the user from the database.
4.  The provided password is checked against the stored password.
5.  If successful, a JWT is generated and a session is created.
6.  A token is returned to the user.
7.  If unsuccessful, an error is returned.
