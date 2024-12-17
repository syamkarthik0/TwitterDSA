import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';

const CacheMonitor = () => {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.error('No authentication token found');
          return;
        }
        const response = await axios.get('http://localhost:8080/api/cache/stats', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        setStats(response.data);
      } catch (error) {
        console.error('Error fetching cache stats:', error);
      }
    };

    fetchStats();
    const interval = setInterval(fetchStats, 5000);
    return () => clearInterval(interval);
  }, []);

  if (!stats) return <div>Loading cache statistics...</div>;

  return (
    <Box sx={{ margin: 2 }}>
      <Paper sx={{ padding: 2 }}>
        <Typography variant="h5" gutterBottom>
          Cache Monitor
        </Typography>
        
        <Box sx={{ marginTop: 2 }}>
          <Typography variant="h6">Cache Statistics</Typography>
          <Typography>
            Size: {stats.size} / {stats.maxSize}
          </Typography>
          <Typography>
            Hit Rate: {(stats.hitRate * 100).toFixed(1)}%
          </Typography>
          <Typography>
            Hits: {stats.cacheHits} | Misses: {stats.cacheMisses}
          </Typography>
        </Box>

        <Box sx={{ marginTop: 2 }}>
          <Typography variant="h6">Most Accessed Users</Typography>
          {stats.mostAccessedUsers.map((user) => (
            <Typography key={user.username}>
              {user.username}: {user.hits} hits (Last access: {new Date(user.lastAccessed).toLocaleTimeString()})
            </Typography>
          ))}
        </Box>
      </Paper>
    </Box>
  );
};

export default CacheMonitor;
