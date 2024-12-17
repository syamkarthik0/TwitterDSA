package com.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "followers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Follower {
    @EmbeddedId
    private FollowerId id;

    @ManyToOne
    @MapsId("followerId")
    @JoinColumn(name = "follower_id")
    private User follower;

    @ManyToOne
    @MapsId("followingId")
    @JoinColumn(name = "following_id")
    private User following;

    @Column(name = "follow_date", nullable = false)
    private LocalDateTime followDate;
}
