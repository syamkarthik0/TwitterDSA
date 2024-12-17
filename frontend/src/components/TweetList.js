import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Box, Paper, Typography, List, ListItem, ListItemText } from '@mui/material';

const TweetList = ({ tweets, setTweets }) => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchTweets = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/tweets', {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
        setTweets(response.data.content || []);
        setLoading(false);
      } catch (error) {
        console.error('Error fetching tweets:', error);
        setError(error.message);
        setLoading(false);
      }
    };

    fetchTweets();
    const interval = setInterval(fetchTweets, 10000); // Refresh every 10 seconds
    return () => clearInterval(interval);
  }, [setTweets]);

  if (loading) return <div>Loading tweets...</div>;
  if (error) return <div>Error loading tweets: {error}</div>;
  if (!tweets || tweets.length === 0) return <div>No tweets available. Create your first tweet!</div>;

  return (
    <Box sx={{ margin: 2 }}>
      <Paper sx={{ padding: 2 }}>
        <Typography variant="h5" gutterBottom>
          Recent Tweets
        </Typography>
        <List>
          {tweets.map((tweet, index) => (
            <ListItem key={tweet.id || index} divider>
              <ListItemText
                primary={tweet.content}
                secondary={`By ${tweet.user?.username || 'Unknown'} at ${tweet.timestamp ? new Date(tweet.timestamp).toLocaleString() : 'Unknown time'}`}
              />
            </ListItem>
          ))}
        </List>
      </Paper>
    </Box>
  );
};

export default TweetList;
