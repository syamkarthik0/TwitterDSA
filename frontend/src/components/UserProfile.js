import React, { useState, useEffect } from 'react';
import { getFollowers, getFollowing } from '../services/followService';
import FollowButton from './FollowButton';
import { useParams } from 'react-router-dom';

const UserProfile = () => {
    const { username } = useParams();
    const [followers, setFollowers] = useState([]);
    const [following, setFollowing] = useState([]);
    const [activeTab, setActiveTab] = useState('tweets');
    const currentUsername = localStorage.getItem('username');
    const isOwnProfile = username === currentUsername;

    const fetchFollowData = async () => {
        try {
            const [followersData, followingData] = await Promise.all([
                getFollowers(),
                getFollowing()
            ]);
            setFollowers(followersData);
            setFollowing(followingData);
        } catch (error) {
            console.error('Error fetching follow data:', error);
        }
    };

    useEffect(() => {
        fetchFollowData();
    }, [username]);

    const handleFollowChange = () => {
        fetchFollowData();
    };

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                <h2>{username}</h2>
                {!isOwnProfile && <FollowButton userId={username} onFollowChange={handleFollowChange} />}
            </div>

            <div style={styles.stats}>
                <div style={styles.stat}>
                    <span style={styles.statCount}>{followers.length}</span>
                    <span style={styles.statLabel}>Followers</span>
                </div>
                <div style={styles.stat}>
                    <span style={styles.statCount}>{following.length}</span>
                    <span style={styles.statLabel}>Following</span>
                </div>
            </div>

            <div style={styles.tabs}>
                <button
                    style={{...styles.tab, ...(activeTab === 'tweets' ? styles.activeTab : {})}}
                    onClick={() => setActiveTab('tweets')}
                >
                    Tweets
                </button>
                <button
                    style={{...styles.tab, ...(activeTab === 'followers' ? styles.activeTab : {})}}
                    onClick={() => setActiveTab('followers')}
                >
                    Followers
                </button>
                <button
                    style={{...styles.tab, ...(activeTab === 'following' ? styles.activeTab : {})}}
                    onClick={() => setActiveTab('following')}
                >
                    Following
                </button>
            </div>

            <div style={styles.content}>
                {activeTab === 'followers' && (
                    <div style={styles.userList}>
                        {followers.map(follower => (
                            <div key={follower.id} style={styles.userItem}>
                                <span>{follower.username}</span>
                                {!isOwnProfile && follower.username !== currentUsername && (
                                    <FollowButton userId={follower.id} />
                                )}
                            </div>
                        ))}
                    </div>
                )}

                {activeTab === 'following' && (
                    <div style={styles.userList}>
                        {following.map(user => (
                            <div key={user.id} style={styles.userItem}>
                                <span>{user.username}</span>
                                {!isOwnProfile && user.username !== currentUsername && (
                                    <FollowButton userId={user.id} />
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

const styles = {
    container: {
        padding: '20px',
        maxWidth: '600px',
        margin: '0 auto'
    },
    header: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '20px'
    },
    stats: {
        display: 'flex',
        gap: '20px',
        marginBottom: '20px'
    },
    stat: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center'
    },
    statCount: {
        fontSize: '18px',
        fontWeight: 'bold'
    },
    statLabel: {
        color: '#536471',
        fontSize: '14px'
    },
    tabs: {
        display: 'flex',
        borderBottom: '1px solid #eee',
        marginBottom: '20px'
    },
    tab: {
        flex: 1,
        padding: '10px',
        border: 'none',
        background: 'none',
        cursor: 'pointer',
        fontSize: '16px',
        color: '#536471'
    },
    activeTab: {
        color: '#1DA1F2',
        borderBottom: '2px solid #1DA1F2'
    },
    userList: {
        display: 'flex',
        flexDirection: 'column',
        gap: '10px'
    },
    userItem: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '10px',
        borderBottom: '1px solid #eee'
    }
};

export default UserProfile;
