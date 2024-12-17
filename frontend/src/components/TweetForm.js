import React, { useState } from 'react';
import { TextField, Button, Box, Typography, CircularProgress } from '@mui/material';
import { createTweet } from '../services/tweetService';

const TweetForm = ({ onTweetCreated }) => {
    const [content, setContent] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!content.trim()) {
            setError('Tweet cannot be empty');
            return;
        }

        try {
            setLoading(true);
            setError('');
            
            const newTweet = await createTweet(content.trim());
            onTweetCreated(newTweet);
            setContent('');
        } catch (error) {
            setError('Failed to create tweet. Please try again.');
            console.error('Error creating tweet:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box component="form" onSubmit={handleSubmit} sx={styles.form}>
            <TextField
                multiline
                rows={3}
                value={content}
                onChange={(e) => setContent(e.target.value)}
                placeholder="What's happening?"
                variant="outlined"
                fullWidth
                error={!!error}
                helperText={error}
                disabled={loading}
                sx={styles.textarea}
                inputProps={{
                    maxLength: 280
                }}
            />
            <Box sx={styles.footer}>
                <Typography variant="caption" color={content.length > 260 ? 'error' : 'textSecondary'}>
                    {content.length}/280
                </Typography>
                <Button
                    type="submit"
                    variant="contained"
                    disabled={loading || !content.trim() || content.length > 280}
                    sx={styles.button}
                >
                    {loading ? <CircularProgress size={24} /> : 'Tweet'}
                </Button>
            </Box>
        </Box>
    );
};

const styles = {
    form: {
        display: 'flex',
        flexDirection: 'column',
        gap: 2
    },
    textarea: {
        '& .MuiOutlinedInput-root': {
            borderRadius: 2,
            backgroundColor: 'background.paper'
        }
    },
    footer: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginTop: 1
    },
    button: {
        borderRadius: 20,
        minWidth: 100
    }
};

export default TweetForm;
