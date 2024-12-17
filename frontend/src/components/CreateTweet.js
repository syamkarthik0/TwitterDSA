import React, { useState } from 'react';
import axios from 'axios';
import { Box, TextField, Button, Paper, Typography } from '@mui/material';

const CreateTweet = ({ onTweetCreated }) => {
  const [content, setContent] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim()) {
      setError('Tweet cannot be empty');
      return;
    }
    if (content.length > 280) {
      setError('Tweet cannot exceed 280 characters');
      return;
    }

    try {
      const token = localStorage.getItem('token');
      if (!token) {
        setError('Please log in to create a tweet');
        return;
      }

      const response = await axios.post('http://localhost:8080/api/tweets', 
        { content },
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );
      setContent('');
      setError('');
      if (onTweetCreated) {
        onTweetCreated(response.data);
      }
    } catch (error) {
      setError(error.response?.data?.error || 'Failed to create tweet');
    }
  };

  return (
    <Box sx={{ margin: 2 }}>
      <Paper sx={{ padding: 2 }}>
        <Typography variant="h6" gutterBottom>
          Create Tweet
        </Typography>
        <form onSubmit={handleSubmit}>
          <TextField
            fullWidth
            multiline
            rows={3}
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="What's happening?"
            error={!!error}
            helperText={error}
            sx={{ marginBottom: 2 }}
          />
          <Button 
            type="submit" 
            variant="contained" 
            color="primary"
            disabled={!content.trim()}
          >
            Tweet
          </Button>
        </form>
      </Paper>
    </Box>
  );
};

export default CreateTweet;
