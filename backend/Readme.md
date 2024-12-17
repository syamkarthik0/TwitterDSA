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
