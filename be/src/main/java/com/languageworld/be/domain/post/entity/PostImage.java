package com.languageworld.be.domain.post.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Table(name = "post_images")
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_image_url", nullable = false)
    private String postImageUrl;

    @Column(nullable = false)
    private int order;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public static PostImage of(String postImageUrl, Integer order, Post post) {

        PostImage postImage = PostImage.builder()
                .postImageUrl(postImageUrl)
                .order(order)
                .post(post)
                .build();

        return postImage;
    }

    public void changeOrder(int order) {
        this.order = order;
    }
}
