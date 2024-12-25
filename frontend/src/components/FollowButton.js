import React, { useState, useEffect } from 'react';
import { Button } from '@mui/material';
import { followUser, unfollowUser, getFollowing } from '../services/followService';

const FollowButton = ({ userId, onFollowChange }) => {
    const [isFollowing, setIsFollowing] = useState(false);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        // Check if the current user is following this user
        const checkFollowStatus = async () => {
            try {
                const following = await getFollowing();
                setIsFollowing(following.some(user => user.id === userId));
            } catch (error) {
                console.error('Error checking follow status:', error);
            }
        };
        checkFollowStatus();
    }, [userId]);

    const handleFollowClick = async () => {
        setLoading(true);
        try {
            if (isFollowing) {
                await unfollowUser(userId);
            } else {
                await followUser(userId);
            }
            setIsFollowing(!isFollowing);
            if (onFollowChange) {
                onFollowChange();
            }
        } catch (error) {
            console.error('Error updating follow status:', error);
        } finally {
            setLoading(false);
        }
    };

    if (!userId) {
        return null;
    }

    return (
        <Button
            variant={isFollowing ? "outlined" : "contained"}
            color="primary"
            onClick={handleFollowClick}
            disabled={loading}
        >
            {loading ? 'Loading...' : (isFollowing ? 'Unfollow' : 'Follow')}
        </Button>
    );
};

export default FollowButton;
