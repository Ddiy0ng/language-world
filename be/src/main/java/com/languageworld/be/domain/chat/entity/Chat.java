package com.languageworld.be.domain.chat.entity;

import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.global.baseEntity.CreatableEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Table(name = "chats")
public class Chat extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatMessage;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroom;


    public static Chat of(String chatMessage, User sender, Chatroom chatroom) {

        return Chat.builder()
                .chatMessage(chatMessage)
                .sender(sender)
                .chatroom(chatroom)
                .build();
    }
}
