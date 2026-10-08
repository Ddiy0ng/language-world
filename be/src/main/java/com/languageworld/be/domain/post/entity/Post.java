package com.languageworld.be.domain.post.entity;

import com.languageworld.be.domain.post.enumGroup.PostTypeCode;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.global.baseEntity.ChangeableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Table(name = "posts")
@Entity
@Getter
public class Post extends ChangeableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostTypeCode postType;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(name = "content_text", nullable = false)
    private String contentText;

    @Column(name = "content_img_url")
    private String contentImgUrl;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
