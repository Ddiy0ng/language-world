package com.languageworld.be.domain.user.entity;

import com.languageworld.be.domain.user.enumGroup.FriendStatusCode;
import com.languageworld.be.global.baseEntity.ChangeableEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Table(name = "friends")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Getter
public class Friend extends ChangeableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Column(name = "friend_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendStatusCode friendStatus;

    public static Friend of(User user, User requestedUser) {

        Friend friend = Friend.builder()
                .user(user)
                .friend(requestedUser)
                .friendStatus(FriendStatusCode.REQUESTED)
                .build();

        return friend;
    }

    public void acceptFriendRequest() {

        this.friendStatus = FriendStatusCode.ACCEPTED;
    }

    public void rejectFriendRequest() {

        this.friendStatus = FriendStatusCode.REJECTED;
    }

}
