package com.languageworld.be.domain.post.entity;

import com.languageworld.be.domain.post.dto.request.CommentRequestDto;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.global.baseEntity.ChangeableEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Table(name = "comments")
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Comment extends ChangeableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_text", nullable = false)
    private String contentText;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public static Comment of(CommentRequestDto commentRequestDto, User author, Post post) {

        Comment comment = Comment.builder()
                .contentText(commentRequestDto.content())
                .author(author).post(post)
                .build();

        return comment;
    }

    public void changeContent(String content) {

        if(content != null)
            contentText = content;
    }
}
