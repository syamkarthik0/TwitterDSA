import React, { useState, useEffect, useRef, useCallback } from "react";
import TweetForm from "./TweetForm";
import Tweet from "./Tweet";
import "./Dashboard.css";

function Dashboard() {
  const [tweets, setTweets] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [lastTweetId, setLastTweetId] = useState(null); // Track the last loaded tweet's ID
  const observer = useRef();

  const fetchTweets = async (startId = null) => {
    if (isLoading) return;

    setIsLoading(true);
    const token = localStorage.getItem("token");
    try {
      const url = new URL("http://localhost:8080/api/tweets");
      if (startId) {
        url.searchParams.append("startId", startId);
      }
      url.searchParams.append("size", "10");

      const response = await fetch(url, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!response.ok) {
        throw new Error("Failed to fetch tweets");
      }

      const data = await response.json();

      if (data.length === 0) {
        setHasMore(false); // No more tweets to load
      } else {
        setTweets((prevTweets) => [...prevTweets, ...data]);
        setLastTweetId(data[data.length - 1].id); // Update last loaded tweet's ID
      }
    } catch (error) {
      console.error("Error fetching tweets:", error);
    } finally {
      setIsLoading(false);
    }
  };

  // Initial load
  useEffect(() => {
    fetchTweets();
  }, []);

  const lastTweetElementRef = useCallback(
    (node) => {
      if (isLoading) return;
      if (observer.current) observer.current.disconnect();

      observer.current = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && hasMore) {
          fetchTweets(lastTweetId); // Load more tweets
        }
      });

      if (node) observer.current.observe(node);
    },
    [isLoading, hasMore, lastTweetId]
  );

  const handleTweetPosted = async () => {
    setTweets([]); // Reset tweets
    setHasMore(true);
    setLastTweetId(null); // Reset pagination
    await fetchTweets(); // Reload tweets
  };

  return (
    <div className="dashboard">
      <TweetForm onTweetPosted={handleTweetPosted} />

      <div className="tweet-list">
        {tweets.map((tweet, index) => {
          if (tweets.length === index + 1) {
            return (
              <div ref={lastTweetElementRef} key={tweet.id}>
                <Tweet tweet={tweet} />
              </div>
            );
          } else {
            return <Tweet key={tweet.id} tweet={tweet} />;
          }
        })}
      </div>

      {isLoading && <div className="loading">Loading...</div>}
      {!hasMore && (
        <div className="no-more-tweets">No more tweets to display.</div>
      )}
    </div>
  );
}

export default Dashboard;
