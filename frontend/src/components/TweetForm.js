import React, { useState } from "react";
import "./TweetForm.css";

function TweetForm({ onTweetPosted }) {
  const [content, setContent] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim()) {
      setError("Tweet cannot be empty");
      return;
    }
    if (content.length > 280) {
      setError("Tweet cannot exceed 280 characters");
      return;
    }

    setIsLoading(true);
    try {
      const token = localStorage.getItem("token");
      const response = await fetch("http://localhost:8080/api/tweets", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ content: content.trim() }),
      });

      if (!response.ok) {
        const data = await response.json();
        throw new Error(data.error || "Failed to post tweet");
      }

      setContent("");
      setError("");
      if (onTweetPosted) {
        onTweetPosted();
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const remainingChars = 280 - content.length;

  return (
    <form className="tweet-form" onSubmit={handleSubmit}>
      <textarea
        className="tweet-input"
        value={content}
        onChange={(e) => {
          setContent(e.target.value);
          setError("");
        }}
        placeholder="What's happening?"
        maxLength={280}
      />
      <div className="tweet-form-footer">
        <span className={`char-count ${remainingChars < 20 ? "warning" : ""}`}>
          {remainingChars}
        </span>
        {error && <div className="error-message">{error}</div>}
        <button
          type="submit"
          className="tweet-button"
          disabled={isLoading || !content.trim() || content.length > 280}
        >
          {isLoading ? "Posting..." : "Tweet"}
        </button>
      </div>
    </form>
  );
}

export default TweetForm;
