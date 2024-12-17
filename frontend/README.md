In the `Login` component, several **data structures** are implicitly used. Let's break down where and how they appear:

---

## **Data Structures in the React Login Component**

### 1. **Hash Map / Object**

**Used for:**

- Managing **form state** in React (`formData`).
- Storing **errors** and user-related data in **localStorage**.

**Example in Code:**

```javascript
const [formData, setFormData] = useState({ username: "", password: "" });
```

- Here, `formData` is a JavaScript **object**, which acts similarly to a **Hash Map** where the keys are `username` and `password`. This helps in efficiently managing multiple input fields in a single state object.

Similarly:

```javascript
localStorage.setItem("token", data.token);
localStorage.setItem("username", data.username);
```

- **localStorage** itself can be considered as a **key-value store** (like a hash map), storing token and username pairs.

---

### 2. **Array**

**Used for:**

- Handling **JSON data** returned from the server, which may include arrays or objects.
- When calling `response.json()`, it often returns complex objects or arrays of data that need to be parsed.

---

### 3. **Queue (Asynchronous Task Queue)**

**Used for:**

- The **event loop** in JavaScript, which manages the asynchronous `fetch` request.
- When the `handleSubmit` function calls the `fetch` API, the request is placed in a **task queue**. The **event loop** ensures the main thread remains responsive by processing these asynchronous requests in order.

---

### 4. **Stack (Call Stack)**

**Used for:**

- Each function call (like `handleChange` or `handleSubmit`) is placed on the **call stack** during execution. When the function finishes execution, it is popped off the stack.

---

### 5. **String**

**Used for:**

- User inputs (username, password) are managed as **strings**.
- **Error messages** are stored and displayed as strings.

**Example in Code:**

```javascript
const [error, setError] = useState("");
```

- Here, `error` is a **string** that stores any error message.

---

### **Summary of Data Structures Used**

| **Data Structure**    | **Usage in the Code**                                      |
| --------------------- | ---------------------------------------------------------- |
| **Hash Map / Object** | Manage `formData` state and store tokens in `localStorage` |
| **Array**             | Handle JSON data from the backend API                      |
| **Queue**             | Manage asynchronous `fetch` requests via the event loop    |
| **Stack**             | Function calls placed on the JavaScript call stack         |
| **String**            | Store input values and error messages                      |

---

In conclusion, this component primarily relies on **objects (hash maps)** to store key-value pairs for state management and **localStorage**. It also utilizes **asynchronous queues** for network requests and the **JavaScript call stack** to manage function executions. These fundamental data structures ensure that the component runs smoothly and efficiently.
