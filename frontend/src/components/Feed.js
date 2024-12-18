import React, { useState, useEffect, useCallback } from 'react';
import { Box, Paper, Typography, CircularProgress, Button } from '@mui/material';
import { getFeed } from '../services/tweetService';
import Tweet from './Tweet';
import TweetForm from './TweetForm';

const Feed = () => {
    const [tweets, setTweets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const currentUsername = localStorage.getItem('username');

    const loadTweets = useCallback(async () => {
        try {
            setLoading(true);
            const response = await getFeed(page);
            const newTweets = response.content || [];
            
            // Extract tweets from UserFeed objects
            const processedTweets = newTweets.map(feedItem => feedItem.tweet);
            
            if (page === 0) {
                setTweets(processedTweets);
            } else {
                setTweets(prev => [...prev, ...processedTweets]);
            }
            
            setHasMore(!response.last);
            setError('');
        } catch (error) {
            console.error('Error loading feed:', error);
            setError('Failed to load feed. Please try again later.');
        } finally {
            setLoading(false);
        }
    }, [page]);

    useEffect(() => {
        loadTweets();
    }, [loadTweets]);

    const handleLoadMore = () => {
        if (!loading && hasMore) {
            setPage(prev => prev + 1);
        }
    };

    const handleNewTweet = (newTweet) => {
        setTweets(prevTweets => [newTweet, ...prevTweets]);
    };

    if (loading && page === 0) {
        return (
            <Box sx={{ margin: 2, display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 200 }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ maxWidth: 600, margin: '0 auto', padding: 2 }}>
            <Paper elevation={3} sx={{ marginBottom: 2, padding: 2 }}>
                <TweetForm onTweetCreated={handleNewTweet} />
            </Paper>

            {error && (
                <Paper elevation={3} sx={{ marginBottom: 2, padding: 2, backgroundColor: '#ffebee' }}>
                    <Typography color="error">{error}</Typography>
                </Paper>
            )}

            {tweets.length === 0 && !loading ? (
                <Paper elevation={3} sx={{ padding: 2, textAlign: 'center' }}>
                    <Typography variant="body1">No tweets in your feed yet. Follow some users to see their tweets!</Typography>
                </Paper>
            ) : (
                <Box>
                    {tweets.map(tweet => (
                        <Box key={tweet.id} sx={{ marginBottom: 2 }}>
                            <Tweet tweet={tweet} username={currentUsername} />
                        </Box>
                    ))}

                    {hasMore && (
                        <Box sx={{ textAlign: 'center', marginTop: 2 }}>
                            <Button
                                variant="outlined"
                                onClick={handleLoadMore}
                                disabled={loading}
                            >
                                {loading ? 'Loading...' : 'Load More'}
                            </Button>
                        </Box>
                    )}
                </Box>
            )}
        </Box>
    );
};

export default Feed;
