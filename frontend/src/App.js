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
import TweetList from "./components/TweetList";
import CreateTweet from "./components/CreateTweet";
import "./App.css";

// Simple Dashboard component
const Dashboard = () => {
  const [username, setUsername] = React.useState("");
  const [tweets, setTweets] = React.useState([]);

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

  const handleTweetCreated = (newTweet) => {
    setTweets((prevTweets) => [newTweet, ...prevTweets]);
  };

  React.useEffect(() => {
    const storedUsername = localStorage.getItem("username");
    if (storedUsername) {
      setUsername(storedUsername);
    }
  }, []);

  return (
    <div style={styles.dashboard}>
      <h1>Welcome, {username}!</h1>
      <p>You have successfully logged in to the protected dashboard.</p>
      <button onClick={handleLogout} style={styles.logoutButton}>
        Logout
      </button>
      <div style={styles.content}>
        <div style={styles.tweetsSection}>
          <CreateTweet onTweetCreated={handleTweetCreated} />
          <TweetList tweets={tweets} setTweets={setTweets} />
        </div>
        <div style={styles.cacheSection}>
          <CacheMonitor />
        </div>
      </div>
    </div>
  );
};

// Protected Route component
const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem("token");

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

function App() {
  return (
    <Router>
      <div style={styles.container}>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />
          <Route path="/" element={<Navigate to="/login" replace />} />
        </Routes>
      </div>
    </Router>
  );
}

const styles = {
  container: {
    minHeight: "100vh",
    backgroundColor: "#f5f5f5",
  },
  dashboard: {
    padding: "20px",
    maxWidth: "1200px",
    margin: "0 auto",
    backgroundColor: "white",
    borderRadius: "8px",
    boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
  },
  content: {
    display: "grid",
    gridTemplateColumns: "2fr 1fr",
    gap: "20px",
    marginTop: "20px",
  },
  tweetsSection: {
    display: "flex",
    flexDirection: "column",
    gap: "20px",
  },
  cacheSection: {
    minWidth: "300px",
  },
  logoutButton: {
    padding: "10px 20px",
    backgroundColor: "#dc3545",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer",
    fontSize: "16px",
    marginTop: "20px",
    ":hover": {
      backgroundColor: "#c82333",
    },
  },
};

export default App;
