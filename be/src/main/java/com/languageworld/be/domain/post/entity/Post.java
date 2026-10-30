package com.languageworld.be.domain.post.entity;

import com.languageworld.be.domain.post.dto.request.PostCreateRequestDto;
import com.languageworld.be.domain.post.dto.request.PostUpdateRequestDto;
import com.languageworld.be.domain.post.enumGroup.PostTypeCode;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.global.baseEntity.ChangeableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Table(name = "posts")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("order ASC")
    private List<PostImage> postImageList = new ArrayList<>();

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<Like> likeList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    public static Post of(PostCreateRequestDto postCreateRequestDto, User author) {

        Post post = Post.builder()
                .postType(postCreateRequestDto.postTypeCode())
                .title(postCreateRequestDto.title())
                .contentText(postCreateRequestDto.contentText())
                .author(author)
                .build();

        return post;
    }

    public void addPostImage(PostImage postImage) {

        postImageList.add(postImage);
    }

    public void removePostImage(PostImage postImage) {

        postImageList.remove(postImage);
    }

    public void changePostTitle(String title) {

        if(title != null)
            this.title = title;

    }

    public void changePostContentText(String contentText) {

        if(contentText != null)
            this.contentText = contentText;
    }
}
