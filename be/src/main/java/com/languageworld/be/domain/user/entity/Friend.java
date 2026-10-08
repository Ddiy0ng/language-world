package com.languageworld.be.domain.user.entity;

import com.languageworld.be.domain.user.enumGroup.FriendStatusCode;
import com.languageworld.be.global.baseEntity.CreatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "friends")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Getter
public class Friend extends CreatableEntity {

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

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
}
