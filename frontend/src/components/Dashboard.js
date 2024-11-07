import React, { useState, useEffect, useCallback } from "react";
import Tweet from "./Tweet";
import TweetForm from "./TweetForm";
import "./Dashboard.css";

function Dashboard() {
  const [tweets, setTweets] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  const loadTweets = useCallback(async () => {
    setIsLoading(true);
    setError("");
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(
        `http://localhost:8080/api/tweets?page=${page}&size=10`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to fetch tweets");
      }

      const data = await response.json();
      setTweets(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      setError("Failed to load tweets. Please try again later.");
      console.error("Error loading tweets:", err);
    } finally {
      setIsLoading(false);
    }
  }, [page]);

  useEffect(() => {
    loadTweets();
  }, [loadTweets]);

  const handleTweetPosted = () => {
    setPage(0); // Reset to first page
    loadTweets();
  };

  const handlePreviousPage = () => {
    setPage((current) => Math.max(0, current - 1));
  };

  const handleNextPage = () => {
    setPage((current) => Math.min(totalPages - 1, current + 1));
  };

  return (
    <div className="dashboard">
      <div className="dashboard-content">
        <TweetForm onTweetPosted={handleTweetPosted} />

        {error && <div className="error-message">{error}</div>}

        {isLoading ? (
          <div className="loading">Loading tweets...</div>
        ) : (
          <>
            <div className="tweet-list">
              {tweets.length === 0 ? (
                <div className="no-tweets">
                  No tweets yet. Be the first to tweet!
                </div>
              ) : (
                tweets.map((tweet) => <Tweet key={tweet.id} tweet={tweet} />)
              )}
            </div>

            {tweets.length > 0 && (
              <div className="pagination">
                <button
                  onClick={handlePreviousPage}
                  disabled={page === 0}
                  className="pagination-button"
                >
                  Previous
                </button>
                <span className="page-info">
                  Page {page + 1} of {totalPages}
                </span>
                <button
                  onClick={handleNextPage}
                  disabled={page >= totalPages - 1}
                  className="pagination-button"
                >
                  Next
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

export default Dashboard;
