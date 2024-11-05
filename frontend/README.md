How to view token:
localStorage.getItem("token") in console of browser

decode in https://jwt.io/

jwt.js:
convert to UTC to convert to local

In the `Login` component, several **data structures** are implicitly used. Let's break down where and how they appear:

---

Let’s walk through the flow of this code step-by-step, combining React/JavaScript concepts with data structure principles to illustrate how the application operates and manages data.

---

### Step 1: **Opening the Application (`App.js` Routing)**

- **Concepts**: React Router, Conditional Rendering, Local Storage (Key-Value Store)
- **Details**: The app begins by rendering the `App` component, which sets up routing for various paths, including `/login`, `/register`, `/dashboard`, and `/`.
- **Flow**:
  - When a user opens the application, `App.js` determines the initial page based on the URL.
  - If they visit `/dashboard`, `App.js` routes this to `ProtectedRoute`, which checks if the user is authenticated (by verifying if a `token` is in `localStorage`).
  - If the `token` exists, it renders the `Dashboard` component; if not, it redirects to `/login` using `Navigate`.

---

### Step 2: **Login Page (`Login.js`)**

- **Concepts**: State Management (`useState`), Event Handling, Form Validation, Data Fetching, Local Storage, Error Handling
- **Details**: The `Login` component is rendered, displaying a form for entering `username` and `password`.
- **Flow**:
  - **State Initialization**: `useState` initializes `formData` (stores username and password), `error` (error messages), and `loading` (loading state).
  - **Form Handling**:
    - When the user types into the form, `handleChange` updates `formData` using the **object spread operator** (`...`), creating a new object without overwriting other properties.
  - **Validation**:
    - `validateEmail` checks the email format using a **regular expression** (pattern matching), ensuring only valid emails are submitted.
  - **Submission**:
    - On form submission, `handleSubmit` sends an HTTP request to the login API using `fetch`.
    - If successful, the token and username are stored in `localStorage`, which is a **key-value store**, and the user is redirected to `/dashboard`.
    - If unsuccessful, `error` is updated, and an error message displays using **conditional rendering** (`error && <p>{error}</p>`).

---

### Step 3: **Dashboard Access (ProtectedRoute Component)**

- **Concepts**: Conditional Rendering, Key-Value Storage (localStorage), Routing, Higher-Order Component
- **Details**: After a successful login, the `ProtectedRoute` component checks for the `token` in `localStorage`.
- **Flow**:
  - **Conditional Rendering**: `ProtectedRoute` renders `Dashboard` if a token is present. If not, it redirects the user to `/login`.
  - **Higher-Order Component**: `ProtectedRoute` is a wrapper for `Dashboard` that adds security by conditionally allowing access based on the user’s authentication status.

---

### Step 4: **Displaying the Dashboard (`Dashboard` Component)**

- **Concepts**: React State, useEffect, Event Handling, Styling, Data Fetching, Local Storage
- **Details**: In the `Dashboard`, `useEffect` fetches the `username` from `localStorage` when the component mounts, displaying a personalized greeting.
- **Flow**:
  - **Fetching Username**: `useEffect` reads `username` from `localStorage` when `Dashboard` loads.
  - **Displaying Username**: This value is stored in the `username` state variable and displayed in the `Dashboard` as part of a greeting message.
  - **Styling**: The `styles` object in `Dashboard` manages the look and feel, setting up the layout and customizing elements like background color and padding for a neat UI.
  - **Logout Function**:
    - The `handleLogout` function is triggered when the user clicks the logout button.
    - **HTTP Request**: `handleLogout` sends a `POST` request to log out the user on the server.
    - **Clearing Data**: If successful, it clears `token` and `username` from `localStorage` and redirects to `/login`.
    - **Error Handling**: If the logout fails, it logs an error to the console, notifying the developer without interrupting the UI.

---

### Step 5: **Returning to Login (on Logout)**

- **Concepts**: Event Handling, Key-Value Store (localStorage), Conditional Rendering
- **Details**: After logging out, `Dashboard` clears authentication data and redirects the user to `/login`.
- **Flow**:
  - `handleLogout` removes `token` and `username` from `localStorage`, effectively logging out the user.
  - The user is then redirected to `/login`, where they can log in again.

---

### Data Structure Concepts Throughout the Flow

1. **Key-Value Store (localStorage)**:

   - **Purpose**: `localStorage` serves as a simple data structure to persist the `token` and `username`, making them accessible even when the user refreshes the page.
   - **Usage**: `localStorage.setItem()` stores data, while `localStorage.getItem()` retrieves it, allowing the app to check if a user is logged in.

2. **Conditional Rendering (ProtectedRoute, Login)**:

   - **Purpose**: Ensures that users only access certain pages based on conditions like authentication status.
   - **Usage**: The conditional in `ProtectedRoute` either renders `Dashboard` or redirects to `/login`.

3. **Boolean State (loading, error)**:

   - **Purpose**: Manages the button state and error messages. For example, `loading` disables the login button while an API request is processing.
   - **Usage**: Booleans in `useState` are used to show or hide loading indicators and error messages based on user actions.

4. **Object Data Structure (formData)**:

   - **Purpose**: `formData` is an object storing `username` and `password`, encapsulating them as related pieces of information for form handling.
   - **Usage**: The spread operator (`...`) in `handleChange` updates `formData` without losing previously entered data, creating a new object on each change.

5. **Regular Expression (validateEmail)**:
   - **Purpose**: `validateEmail` uses a regex pattern to check the format of the email entered.
   - **Usage**: This pattern match helps validate emails efficiently, avoiding unnecessary requests to the server for incorrect inputs.

---

### Summary of the Flow

1. **Initialization and Routing**: `App.js` sets up routing, directing users based on their authentication status. `ProtectedRoute` prevents unauthorized access to the `Dashboard`.

2. **Login Process**:

   - The `Login` component handles user input, validating the email format, and makes an API request to authenticate.
   - On success, the app stores the user’s token and redirects to the `Dashboard`.

3. **Displaying the Dashboard**:

   - The `Dashboard` uses `useEffect` to load the `username`, displays a personalized greeting, and provides a logout button.
   - Logging out removes user data from `localStorage` and returns the user to the login page.

4. **Data Structure Integration**:
   - `localStorage` acts as a key-value store, `useState` manages the form and loading states, and `validateEmail` uses regex for data validation.
   - `ProtectedRoute` applies conditional rendering based on authentication status, while `formData` is handled as an object to hold multiple related pieces of information (username and password).

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
