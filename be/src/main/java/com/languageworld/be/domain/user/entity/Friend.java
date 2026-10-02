package com.languageworld.be.domain.user.entity;

import com.languageworld.be.domain.user.enumGroup.FriendStatusCode;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Table(name = "friends")
@Entity
@Getter
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Column(name = "friend_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendStatusCode friendStatus;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
}
