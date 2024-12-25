import React, { useState, useEffect } from 'react';
import { Box, Paper, Typography, CircularProgress } from '@mui/material';
import Tweet from './Tweet';
import TweetForm from './TweetForm';
import { getFeed } from '../services/tweetService';

const Feed = () => {
    const [tweets, setTweets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const currentUsername = localStorage.getItem('username');
<<<<<<< HEAD
    const [page, setPage] = useState(0); // For pagination
=======
    const REFRESH_INTERVAL = 10000; // Refresh every 10 seconds
>>>>>>> f287bc799193aa5019c53b42aa41c4f993869bbe


    // Fetch feed on component mount and when page changes
    useEffect(() => {
<<<<<<< HEAD
        const fetchFeed = async () => {
            try {
                setLoading(true);
                const feedTweets = await getFeed(page); // Fetch feed using page number
    
                // Filter out duplicate tweets based on tweet id
                setTweets(prevTweets => {
                    const tweetIds = new Set(prevTweets.map(tweet => tweet.id));
                    const uniqueTweets = feedTweets.filter(tweet => !tweetIds.has(tweet.id));
                    return [...prevTweets, ...uniqueTweets]; // Append only unique tweets
                });
            } catch (error) {
                setError(error.message || 'Failed to load feed');
            } finally {
                setLoading(false);
            }
        };
    
        fetchFeed();
    }, [page]);
    
=======
        // Initial load
        loadTweets();

        // Set up auto-refresh interval
        const intervalId = setInterval(loadTweets, REFRESH_INTERVAL);

        // Cleanup interval on component unmount
        return () => clearInterval(intervalId);
    }, []); // Empty dependency array means this effect runs once on mount
>>>>>>> f287bc799193aa5019c53b42aa41c4f993869bbe

    const loadTweets = async () => {
        try {
            setLoading(true);
            const response = await getFeed();
            const feedTweets = response.content || [];
            
            setTweets(feedTweets);
            setError(null);
        } catch (err) {
            console.error('Error loading tweets:', err);
            setError('Failed to load tweets. Please try again later.');
        } finally {
            setLoading(false);
        }
    };

    const handleNewTweet = (newTweet) => {
        setTweets(prevTweets => {
            if (prevTweets.some(tweet => tweet.id === newTweet.id)) {
                return prevTweets; // If the tweet already exists, return the same list
            }
            return [newTweet, ...prevTweets]; // Otherwise, add the new tweet
        });
    };
    

    if (loading && tweets.length === 0) {
        return (
            <Box sx={{ margin: 2, display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 200 }}>
                <CircularProgress />
            </Box>
        );
    }

    if (error) {
        return (
            <Paper elevation={3} sx={{ marginBottom: 2, padding: 2, backgroundColor: '#ffebee' }}>
                <Typography color="error">{error}</Typography>
            </Paper>
        );
    }

    return (
        <Box sx={{ maxWidth: 600, margin: '0 auto', padding: 2 }}>
            <Paper elevation={3} sx={{ marginBottom: 2, padding: 2 }}>
                <TweetForm onTweetCreated={handleNewTweet} />
            </Paper>

            {tweets.length === 0 ? (
                <Paper elevation={3} sx={{ padding: 2, textAlign: 'center' }}>
                    <Typography variant="body1">No tweets in your feed yet. Follow some users to see their tweets!</Typography>
                </Paper>
            ) : (
                <Box>
                    {loading && (
                        <Box sx={{ display: 'flex', justifyContent: 'center', padding: 1 }}>
                            <CircularProgress size={20} />
                        </Box>
                    )}
                    {tweets.map(tweet => (
                        <Box key={tweet.id} sx={{ marginBottom: 2 }}>
                            <Tweet tweet={tweet} username={currentUsername} />
                        </Box>
                    ))}
                </Box>
            )}
        </Box>
    );
};

export default Feed;
