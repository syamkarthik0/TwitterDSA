const API_URL = 'http://localhost:8081/api/follow';

export const followUser = async (followingId) => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('Please log in to follow users');
        }

        const response = await fetch(`${API_URL}/follow/${followingId}`, {
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

        const response = await fetch(`${API_URL}/${followingId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to unfollow user');
        }
    } catch (error) {
        console.error('Error unfollowing user:', error);
        throw error;
    }
};

export const getFollowers = async () => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('Please log in to view followers');
        }

        const response = await fetch(`${API_URL}/followers`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to fetch followers');
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching followers:', error);
        throw error;
    }
};

export const getFollowing = async () => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('Please log in to view following');
        }

        const response = await fetch(`${API_URL}/following`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to fetch following');
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching following:', error);
        throw error;
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
