const API_URL = 'http://localhost:8081/api';

export const followUser = async (userId) => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('No authentication token found');
        }

        if (!userId) {
            console.warn('Missing user ID for follow:', { userId });
            return false;
        }

        const response = await fetch(`${API_URL}/follow/${userId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to follow user');
        }
        
        return await response.json();
    } catch (error) {
        console.error('Error following user:', error);
        // Return false instead of throwing error to handle the case gracefully
        return false;
    }
};

export const unfollowUser = async (userId) => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('No authentication token found');
        }

        if (!userId) {
            console.warn('Missing user ID for unfollow:', { userId });
            return false;
        }

        const response = await fetch(`${API_URL}/follow/${userId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to unfollow user');
        }
        
        return await response.json();
    } catch (error) {
        console.error('Error unfollowing user:', error);
        // Return false instead of throwing error to handle the case gracefully
        return false;
    }
};

export const getFollowers = async () => {
    try {
        const token = localStorage.getItem('token');
        const userId = localStorage.getItem('userId');
        if (!token || !userId) {
            throw new Error('No authentication token or user ID found');
        }

        const response = await fetch(`${API_URL}/follow/followers/${userId}`, {
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
        // Return false instead of throwing error to handle the case gracefully
        return false;
    }
};

export const getFollowing = async () => {
    try {
        const token = localStorage.getItem('token');
        const userId = localStorage.getItem('userId');
        if (!token || !userId) {
            throw new Error('No authentication token or user ID found');
        }

        const response = await fetch(`${API_URL}/follow/following/${userId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to get following users');
        }
        
        return await response.json();
    } catch (error) {
        console.error('Error getting following users:', error);
        // Return false instead of throwing error to handle the case gracefully
        return false;
    }
};

export const checkFollowing = async (currentUserId, targetUserId) => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.warn('No authentication token found');
            return undefined;
        }

        if (!currentUserId || !targetUserId) {
            console.warn('Missing user IDs for follow check:', { currentUserId, targetUserId });
            return undefined;
        }

        const response = await fetch(`${API_URL}/follow/check/${currentUserId}/${targetUserId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to check following status');
        }
        
        return await response.json();
    } catch (error) {
        console.error('Error checking following status:', error);
        throw error;
    }
};
