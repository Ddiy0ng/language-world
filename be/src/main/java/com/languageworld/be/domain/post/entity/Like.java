package com.languageworld.be.domain.post.entity;

import com.languageworld.be.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Table(name = "likes")
@Entity
@Getter
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
