const API_URL = 'http://localhost:8081/api/follow';

export const followUser = async (followingId) => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('Please log in to follow users');
        }

        const response = await fetch(`${API_URL}/${followingId}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to follow user');
        }
    } catch (error) {
        console.error('Error following user:', error);
        throw error;
    }
};

export const unfollowUser = async (followingId) => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('Please log in to unfollow users');
        }

        // Ensure followingId is a number
        const followingIdNum = parseInt(followingId, 10);
        if (isNaN(followingIdNum)) {
            throw new Error('Invalid user ID');
        }

        const response = await fetch(`${API_URL}/${followingIdNum}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => null);
            throw new Error(errorData?.error || 'Failed to unfollow user');
        }
    } catch (error) {
        console.error('Error unfollowing user:', error);
        throw error;
    }
};

export const getFollowers = async (userId) => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('Please log in to view followers');
        }

        const response = await fetch(`${API_URL}/followers/${userId || 'me'}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to get followers');
        }

        return await response.json();
    } catch (error) {
        console.error('Error getting followers:', error);
        throw error;
    }
};

export const getFollowing = async (userId) => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('Please log in to view following');
        }

        // If userId is 'me', use the stored userId from localStorage
        const currentUserId = userId === 'me' ? localStorage.getItem('userId') : userId;
        if (!currentUserId) {
            throw new Error('User ID not found. Please log in again.');
        }

        const response = await fetch(`${API_URL}/following/${currentUserId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to get following');
        }

        return await response.json();
    } catch (error) {
        console.error('Error getting following:', error);
        throw error;
    }
};

// Helper function to get current user ID from token
const getCurrentUserId = () => {
    const token = localStorage.getItem('token');
    if (!token) return null;
    
    try {
        // JWT tokens are in format: header.payload.signature
        const payload = token.split('.')[1];
        const decodedPayload = JSON.parse(atob(payload));
        return decodedPayload.userId; // Assuming the user ID is stored in the token
    } catch (error) {
        console.error('Error decoding token:', error);
        return null;
    }
};

export const getSuggestedUsers = async (maxSuggestions = 5) => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('Please log in to view suggestions');
        }

        const response = await fetch(`${API_URL}/suggestions?maxSuggestions=${maxSuggestions}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to fetch suggestions');
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching suggestions:', error);
        throw error;
    }
};

export const getFeedUsers = async () => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('Please log in to view feed');
        }

        const response = await fetch(`${API_URL}/feed-users`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to fetch feed users');
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching feed users:', error);
        throw error;
    }
};
