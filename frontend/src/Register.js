import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const Register = () => {
  const [formData, setFormData] = useState({ username: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError("");
  };

  const validateEmail = (email) =>
    /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateEmail(formData.username)) {
      setError("Invalid email format");
      return;
    }

    if (!formData.password || formData.password.trim() === "") {
      setError("Password cannot be empty");
      return;
    }

    setLoading(true);
    try {
      const response = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: formData.username.trim(),
          password: formData.password.trim()
        }),
        credentials: "include",
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || "Registration failed");
      }

      // Redirect to login after successful registration
      navigate("/login");
    } catch (err) {
      setError(err.message || "An error occurred during registration");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <form onSubmit={handleSubmit} style={styles.form}>
        <h2 style={styles.title}>Register</h2>
        <input
          type="text"
          name="username"
          placeholder="Email"
          value={formData.username}
          onChange={handleChange}
          style={styles.input}
          disabled={loading}
          autoComplete="email"
        />
        <input
          type="password"
          name="password"
          placeholder="Password"
          value={formData.password}
          onChange={handleChange}
          style={styles.input}
          disabled={loading}
          autoComplete="new-password"
        />
        <button type="submit" style={styles.button} disabled={loading}>
          {loading ? "Registering..." : "Register"}
        </button>
        {error && <p style={styles.error}>{error}</p>}
        <button
          type="button"
          onClick={() => navigate("/login")}
          style={styles.linkButton}
        >
          Already have an account? Login
        </button>
      </form>
    </div>
  );
};

const styles = {
  container: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    minHeight: "100vh",
    backgroundColor: "#f5f5f5",
  },
  form: {
    display: "flex",
    flexDirection: "column",
    gap: "15px",
    padding: "30px",
    backgroundColor: "white",
    borderRadius: "8px",
    boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
    width: "100%",
    maxWidth: "400px",
  },
  title: {
    textAlign: "center",
    margin: "0 0 20px 0",
    color: "#333",
  },
  input: {
    padding: "12px",
    border: "1px solid #ddd",
    borderRadius: "4px",
    fontSize: "16px",
  },
  button: {
    padding: "12px",
    backgroundColor: "#007bff",
    color: "white",
    border: "none",
    borderRadius: "4px",
    fontSize: "16px",
    cursor: "pointer",
    ":hover": {
      backgroundColor: "#0056b3",
    },
    ":disabled": {
      backgroundColor: "#cccccc",
      cursor: "not-allowed",
    },
  },
  error: {
    color: "red",
    textAlign: "center",
    margin: "10px 0 0 0",
  },
  linkButton: {
    background: "none",
    border: "none",
    color: "#007bff",
    cursor: "pointer",
    textDecoration: "underline",
    fontSize: "14px",
  },
};

export default Register;
