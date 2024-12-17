import React, { useState, useEffect } from 'react';
import { followUser, unfollowUser, checkFollowing } from '../services/followService';

const FollowButton = ({ userId, onFollowChange }) => {
    const [isFollowing, setIsFollowing] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        const checkFollowStatus = async () => {
            try {
                if (!userId) {
                    console.warn('No user ID provided to FollowButton');
                    return;
                }

                const currentUserId = localStorage.getItem('userId');
                if (!currentUserId) {
                    console.warn('No current user ID found in localStorage');
                    return;
                }

                const status = await checkFollowing(currentUserId, userId);
                if (status !== undefined) {
                    setIsFollowing(status);
                    setError(null);
                }
            } catch (error) {
                console.error('Error checking follow status:', error);
                setError('Failed to check follow status');
            }
        };

        checkFollowStatus();
    }, [userId]);

    const handleFollowClick = async () => {
        try {
            if (!userId) {
                throw new Error('No user ID provided');
            }

            setIsLoading(true);
            setError(null);

            if (isFollowing) {
                await unfollowUser(userId);
                setIsFollowing(false);
            } else {
                await followUser(userId);
                setIsFollowing(true);
            }

            if (onFollowChange) {
                onFollowChange(!isFollowing);
            }
        } catch (error) {
            console.error('Error toggling follow:', error);
            if (error.message.includes('Already following')) {
                setIsFollowing(true);
            } else {
                setError('Failed to update follow status');
            }
        } finally {
            setIsLoading(false);
        }
    };

    if (!userId) {
        return null;
    }

    return (
        <div>
            <button
                onClick={handleFollowClick}
                disabled={isLoading}
                style={{
                    padding: '8px 16px',
                    borderRadius: '20px',
                    border: isFollowing ? '1px solid #1DA1F2' : 'none',
                    backgroundColor: isFollowing ? 'white' : '#1DA1F2',
                    color: isFollowing ? '#1DA1F2' : 'white',
                    fontWeight: 'bold',
                    cursor: isLoading ? 'not-allowed' : 'pointer',
                    opacity: isLoading ? 0.7 : 1,
                    transition: 'all 0.2s ease'
                }}
            >
                {isLoading ? 'Loading...' : isFollowing ? 'Following' : 'Follow'}
            </button>
            {error && <div style={{ color: 'red', marginTop: '4px', fontSize: '12px' }}>{error}</div>}
        </div>
    );
};

export default FollowButton;
