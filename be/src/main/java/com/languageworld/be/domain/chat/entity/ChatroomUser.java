package com.languageworld.be.domain.chat.entity;

import com.languageworld.be.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Table(name = "chatroom_users", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "chatroom_id"}))
public class ChatroomUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroom;

    @Column(name = "entered_at")
    private LocalDateTime enteredAt;

    @Column(name = "quit_at")
    private LocalDateTime quitAt;

    public static ChatroomUser of(User user, Chatroom chatroom) {

        return ChatroomUser.builder()
                .user(user)
                .chatroom(chatroom)
                .enteredAt(LocalDateTime.now())
                .quitAt(null)
                .build();
    }

    public void reEnterChatroom() {

        this.enteredAt = LocalDateTime.now();
        this.quitAt = null;
    }

    public void quitChatroom() {

        this.quitAt = LocalDateTime.now();
    }
}
