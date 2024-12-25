import React from 'react';
import { Card, CardContent, Typography, Box, Avatar } from '@mui/material';
import { formatDistanceToNow } from 'date-fns';
import FollowButton from './FollowButton';

const Tweet = ({ tweet, username: currentUsername, onFollowChange }) => {
    // Add null checks
    if (!tweet || !tweet.user) {
        return null;
    }

    const { user, content, timestamp } = tweet;
    const isOwnTweet = user.username === currentUsername;

    // Format the timestamp
    const getTimeAgo = () => {
        try {
            if (!timestamp) return '';
            const date = new Date(timestamp);
            if (isNaN(date.getTime())) return ''; // Invalid date
            return formatDistanceToNow(date, { addSuffix: true });
        } catch (error) {
            console.error('Error formatting timestamp:', error);
            return '';
        }
    };

    return (
        <Card elevation={2}>
            <CardContent>
                <Box display="flex" alignItems="center" justifyContent="space-between" mb={1}>
                    <Box display="flex" alignItems="center">
                        <Avatar sx={{ marginRight: 1 }}>
                            {user.username.charAt(0).toUpperCase()}
                        </Avatar>
                        <Typography variant="subtitle1" component="span" fontWeight="bold">
                            {user.username}
                        </Typography>
                    </Box>
                    {!isOwnTweet && (
                        <FollowButton 
                            userId={user.id} 
                            onFollowChange={onFollowChange}
                        />
                    )}
                </Box>
                <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap' }}>
                    {content}
                </Typography>
                <Typography variant="caption" color="textSecondary">
                    {getTimeAgo()}
                </Typography>
            </CardContent>
        </Card>
    );
};

export default Tweet;
