package com.languageworld.be.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Table(name = "blocked_users")
@Entity
@Getter
public class BlockedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "blocked_user_id", nullable = false)
    private User blockedUser;

    @Column(name = "blocked_at", nullable = false)
    private LocalDateTime blockedAt;
}
