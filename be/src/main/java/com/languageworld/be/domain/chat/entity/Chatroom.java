package com.languageworld.be.domain.chat.entity;

import com.languageworld.be.domain.chat.enumGroup.ChatroomTypeCode;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.global.baseEntity.CreatableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Table(name = "chatrooms")
public class Chatroom extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "chatroom_type", nullable = false)
    private ChatroomTypeCode chatroomType;

    @Max(100)
    @Column(name = "chatroom_size", nullable = false)
    private int chatroomSize;

    @Column(name = "chatroom_title", length = 30)
    private String chatroomTitle;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;

    @Builder.Default
    @OneToMany(
            mappedBy = "chatroom",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<Chat> chatList = new ArrayList<>();

    @Builder.Default
    @OneToMany(
            mappedBy = "chatroom",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<ChatroomUser> chatroomUserList = new ArrayList<>();

    public static Chatroom of(ChatroomTypeCode chatroomType, int chatroomSize, String chatroomTitle, User host) {

        return Chatroom.builder()
                .chatroomType(chatroomType)
                .chatroomSize(chatroomSize)
                .chatroomTitle(chatroomTitle)
                .host(host)
                .build();
    }

    public void changeHost(User newHost) {

        host = newHost;
    }
}
