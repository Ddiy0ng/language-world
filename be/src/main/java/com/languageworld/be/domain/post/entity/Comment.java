package com.languageworld.be.domain.post.entity;

import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.global.baseEntity.ChangeableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Table(name = "comments")
@Entity
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

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
