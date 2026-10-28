package com.languageworld.be.domain.user.entity;

import com.languageworld.be.global.baseEntity.ChangeableEntity;
import com.languageworld.be.global.baseEntity.CreatableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Table(name = "blocked_users")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Getter
public class BlockedUser extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "blocked_user_id", nullable = false)
    private User blockedUser;

    public static BlockedUser of(User user, User requestedUser) {

        BlockedUser blockedUser = BlockedUser.builder()
                .user(user)
                .blockedUser(requestedUser)
                .build();

        return blockedUser;
    }
}
