import React from "react";
import "./Tweet.css";

function formatTimestamp(timestamp) {
  const date = new Date(timestamp);
  return date.toLocaleString();
}

function Tweet({ tweet }) {
  return (
    <div className="tweet">
      <div className="tweet-header">
        <span className="username">{tweet.user.username}</span>
        <span className="timestamp">{formatTimestamp(tweet.timestamp)}</span>
      </div>
      <div className="tweet-content">{tweet.content}</div>
    </div>
  );
}

export default Tweet;
