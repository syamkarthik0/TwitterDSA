import React from 'react';
import { Link } from 'react-router-dom';
import FollowButton from './FollowButton';

const Tweet = ({ tweet, username: currentUsername }) => {
    // Add null checks
    if (!tweet || !tweet.user) {
        return null;
    }

    const isOwnTweet = tweet.user.username === currentUsername;
    // Use timestamp from backend
    const formattedDate = new Date(tweet.timestamp).toLocaleString();

    return (
        <div style={styles.tweet}>
            <div style={styles.header}>
                <Link 
                    to={`/profile/${tweet.user.username}`}
                    style={styles.username}
                >
                    {tweet.user.username}
                </Link>
                {!isOwnTweet && tweet.user.id && (
                    <FollowButton userId={tweet.user.id} />
                )}
            </div>
            <p style={styles.content}>{tweet.content}</p>
            <span style={styles.timestamp}>{formattedDate}</span>
        </div>
    );
};

const styles = {
    tweet: {
        padding: '15px',
        borderBottom: '1px solid #eee',
        backgroundColor: 'white',
        borderRadius: '10px',
        boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
    },
    header: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '10px'
    },
    username: {
        fontWeight: 'bold',
        color: '#1DA1F2',
        textDecoration: 'none',
        '&:hover': {
            textDecoration: 'underline'
        }
    },
    content: {
        margin: '10px 0',
        lineHeight: '1.5'
    },
    timestamp: {
        fontSize: '12px',
        color: '#536471'
    }
};

export default Tweet;
