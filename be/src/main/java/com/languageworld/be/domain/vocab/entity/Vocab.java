package com.languageworld.be.domain.vocab.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Table(name = "vocabs")
@Entity
@Getter
public class Vocab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vocab;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
}
