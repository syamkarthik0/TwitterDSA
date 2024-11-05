## Detailed Architecture Document for Spring Boot Authentication Application

This document outlines the architecture of a Spring Boot application designed for user authentication and authorization. It leverages a RESTful API architecture, Spring Security, Spring Data JPA, and JSON Web Tokens (JWTs) to provide a secure and robust solution.

### 1. Overview

The application provides a set of API endpoints for user registration, login, and logout. It uses JWTs for authentication, storing user data in an H2 in-memory database. The application follows a layered architecture, separating concerns and promoting modularity.

### 2. Architectural Layers

#### 2.1 Presentation Layer

- **Controllers:** The presentation layer is responsible for handling incoming HTTP requests and generating responses. This is achieved through controllers like `AuthController`, which:
  - Receive requests for various authentication operations (register, login, logout).
  - Parse request data (e.g., username, password).
  - Delegate processing to underlying services (`AuthService`, `JwtUtil`, `SessionManager`).
  - Assemble and return responses (including JWTs, user details, or error messages).

#### 2.2 Service Layer

- **`AuthService`:** This service forms the core of the authentication logic:
  - **Registration:** Verifies username uniqueness, encrypts passwords using `PasswordEncoder`, sets default user roles, and persists user data to the database via `UserRepository`.
  - **Login:** Retrieves user details from the database, validates passwords using `PasswordEncoder`, and returns user information upon successful authentication.
  - **Logout:** Invalidates the user's session in `SessionManager`.
- **`JwtUtil`:** Responsible for JWT management:
  - Generates JWTs containing user details upon successful authentication.
  - Validates JWTs presented in requests.
  - Extracts information (username, expiration) from JWTs.
- **`SessionManager`:** Manages user sessions using JWTs:
  - Creates sessions by associating JWTs with logged-in users.
  - Invalidates sessions upon logout.
  - Verifies session validity by checking the presence and correctness of JWTs.

#### 2.3 Repository Layer

- **`UserRepository`:** This interface extends Spring Data JPA's `JpaRepository`, enabling interaction with the H2 database. It provides methods for:
  - Finding users by username (`findByUsername`).
  - Checking for the existence of usernames (`existsByUsername`).
  - Persisting and managing `User` entities.

#### 2.4 Persistence Layer

- **H2 Database:** An in-memory, relational database used for data storage. It is well-suited for development and testing due to its ease of setup and use. Data is structured according to the `User` entity, which defines attributes like username, password, and role.

#### 2.5 Security Layer

- **Spring Security:** The framework used to secure the application.
  - **`SecurityConfig`:** Centralized configuration for security settings:
    - Defines authentication mechanisms (JWT-based).
    - Sets authorization rules (e.g., permit all requests to `/api/auth/**`, require authentication for others).
    - Configures CORS (Cross-Origin Resource Sharing) to allow requests from specific origins (e.g., a React frontend).
  - **`JwtRequestFilter`:** Intercepts incoming requests:
    - Extracts JWTs from the `Authorization` header.
    - Validates JWTs using `JwtUtil`.
    - Sets up the security context for authenticated requests, allowing access to protected resources.

### 3. Technology Stack

- **Java:** The primary programming language used for the application.
- **Spring Boot:** The foundation of the application, providing auto-configuration, dependency management, and embedded server capabilities.
- **Spring Security:** Framework for handling authentication, authorization, and other security features.
- **Spring Data JPA:** Simplifies interaction with the database by providing a higher-level abstraction over JDBC.
- **H2 Database:** The chosen relational database, providing in-memory data storage.
- **JWT (JSON Web Token):** Standard for secure information exchange, used here for authentication.
- **Maven:** Build automation tool and dependency manager.
- **Lombok:** Reduces boilerplate code through annotations (not shown in provided sources, but mentioned in the dependencies).

### 4. Flow of Operations

1.  **Client Request:** A client sends an HTTP request to the application, targeting an authentication endpoint (e.g., `/api/auth/login`).
2.  **Request Handling:** Spring Boot routes the request to the appropriate controller (`AuthController`) based on the URL and HTTP method.
3.  **Authentication Logic:** The controller delegates the request to `AuthService`, which performs the necessary authentication steps (registration, login, or logout).
4.  **Database Interaction:** `AuthService` uses `UserRepository` to interact with the H2 database, fetching or persisting user data as needed.
5.  **JWT Generation/Validation:** For successful login or registration, `JwtUtil` generates a JWT. For all authenticated requests, `JwtRequestFilter` validates the presented JWT.
6.  **Session Management:** `SessionManager` manages user sessions by storing and verifying JWTs, ensuring that only authenticated users access protected resources.
7.  **Response Generation:** The controller assembles a response, which may include a JWT, user details, or error messages, and sends it back to the client.

### 5. Security Considerations

- **Password Storage:** Passwords are stored securely using a `PasswordEncoder` (likely `BCryptPasswordEncoder`), which hashes them to protect sensitive information.
- **JWT Security:** JWTs are signed with a secret key to ensure their integrity and prevent tampering. They are validated for each request to prevent unauthorized access.
- **Session Management:** `SessionManager` actively manages user sessions, invalidating them upon logout to prevent unauthorized access from stale tokens.
- **CORS Configuration:** `SecurityConfig` defines CORS settings to control which origins can access the API, mitigating the risk of cross-site scripting attacks.

### 6. Deployment

The application is built using Maven, which manages dependencies and packages the application into an executable JAR file. It can be deployed on any platform that supports Java and can be run using an embedded server (like Tomcat or Jetty) provided by Spring Boot.

### 7. Conclusion

This architecture document describes a secure and well-structured Spring Boot application for user authentication and authorization. The use of Spring Security, Spring Data JPA, JWTs, and a layered architecture ensures robust functionality, maintainability, and security. This documentation serves as a comprehensive guide to understanding the application's design and implementation.
