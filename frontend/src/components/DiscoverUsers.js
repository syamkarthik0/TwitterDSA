import React, { useState, useEffect } from 'react';
import { Box, Paper, Typography, List, ListItem, ListItemText, ListItemSecondaryAction, CircularProgress } from '@mui/material';
import FollowButton from './FollowButton';

const DiscoverUsers = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const currentUsername = localStorage.getItem('username');

    const fetchUsers = async () => {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                throw new Error('No authentication token found');
            }

            const response = await fetch('http://localhost:8081/api/users', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                throw new Error('Failed to fetch users');
            }

            const data = await response.json();
            // Filter out the current user
            setUsers(data.filter(user => user.username !== currentUsername));
            setError('');
        } catch (error) {
            console.error('Error fetching users:', error);
            setError('Failed to load users. Please try again later.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, [currentUsername]);

    const handleFollowChange = () => {
        // Refresh the user list after follow/unfollow
        fetchUsers();
    };

    if (loading) {
        return (
            <Box sx={{ margin: 2, display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: 200 }}>
                <CircularProgress />
            </Box>
        );
    }

    if (error) {
        return (
            <Box sx={{ margin: 2 }}>
                <Paper sx={{ padding: 2 }}>
                    <Typography color="error" align="center">
                        {error}
                    </Typography>
                </Paper>
            </Box>
        );
    }

    return (
        <Box sx={{ margin: 2 }}>
            <Paper sx={{ padding: 2 }}>
                <Typography variant="h6" gutterBottom>
                    Discover Users
                </Typography>
                <List>
                    {users.length === 0 ? (
                        <Typography color="textSecondary" align="center" sx={{ py: 2 }}>
                            No users found to follow
                        </Typography>
                    ) : (
                        users.map(user => (
                            <ListItem key={user.id} divider>
                                <ListItemText 
                                    primary={user.username}
                                />
                                <ListItemSecondaryAction>
                                    <FollowButton 
                                        userId={user.id} 
                                        onFollowChange={handleFollowChange}
                                    />
                                </ListItemSecondaryAction>
                            </ListItem>
                        ))
                    )}
                </List>
            </Paper>
        </Box>
    );
};

export default DiscoverUsers;
