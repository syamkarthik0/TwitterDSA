const API_URL = 'http://localhost:8080/api';

export const createTweet = async (content) => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('Please log in to create a tweet');
        }

        const response = await fetch(`${API_URL}/tweets`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ content })
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to create tweet');
        }

        return await response.json();
    } catch (error) {
        console.error('Error creating tweet:', error);
        throw error;
    }
};

export const getFeed = async (page = 0, size = 10) => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('Please log in to view your feed');
        }

        const response = await fetch(`${API_URL}/feed?page=${page}&size=${size}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to fetch feed');
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching feed:', error);
        throw error;
    }
};

export const getUserTweets = async (userId, page = 0, size = 10) => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('Please log in to view tweets');
        }

        const response = await fetch(`${API_URL}/tweets/user/${userId}?page=${page}&size=${size}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to fetch user tweets');
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching user tweets:', error);
        throw error;
    }
};
