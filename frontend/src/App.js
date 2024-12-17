import React from "react";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  Navigate,
} from "react-router-dom";
import Login from "./Login";
import Register from "./Register";
import CacheMonitor from "./components/CacheMonitor";
import Feed from "./components/Feed";
import UserProfile from "./components/UserProfile";
import DiscoverUsers from "./components/DiscoverUsers";
import "./App.css";

// Dashboard component with new Feed
const Dashboard = () => {
  const [username, setUsername] = React.useState("");

  const handleLogout = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        credentials: "include",
      });

      if (response.ok) {
        localStorage.removeItem("token");
        localStorage.removeItem("username");
        window.location.href = "/login";
      }
    } catch (error) {
      console.error("Logout failed:", error);
    }
  };

  React.useEffect(() => {
    const storedUsername = localStorage.getItem("username");
    if (storedUsername) {
      setUsername(storedUsername);
    }
  }, []);

  return (
    <div style={styles.dashboard}>
      <div style={styles.header}>
        <h1>Welcome, {username}!</h1>
        <button onClick={handleLogout} style={styles.logoutButton}>
          Logout
        </button>
      </div>
      <div style={styles.content}>
        <div style={styles.feed}>
          <Feed />
        </div>
        <div style={styles.discover}>
          <DiscoverUsers />
        </div>
      </div>
    </div>
  );
};

// Protected Route component
const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem("token");
  if (!token) {
    return <Navigate to="/login" />;
  }
  return children;
};

const App = () => {
  return (
    <Router>
      <div style={styles.container}>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile/:username"
            element={
              <ProtectedRoute>
                <UserProfile />
              </ProtectedRoute>
            }
          />
          <Route
            path="/cache"
            element={
              <ProtectedRoute>
                <CacheMonitor />
              </ProtectedRoute>
            }
          />
        </Routes>
      </div>
    </Router>
  );
};

const styles = {
  container: {
    minHeight: "100vh",
    backgroundColor: "#f5f5f5",
  },
  dashboard: {
    padding: "20px",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "20px",
  },
  content: {
    display: "flex",
    gap: "20px",
    flexDirection: "row",
    maxWidth: "1200px",
    margin: "0 auto",
    padding: "0 20px"
  },
  feed: {
    flex: "2"
  },
  discover: {
    flex: "1",
    minWidth: "300px"
  },
  logoutButton: {
    padding: "10px 20px",
    backgroundColor: "#1DA1F2",
    color: "white",
    border: "none",
    borderRadius: "20px",
    cursor: "pointer",
    fontWeight: "bold",
  },
};

export default App;
