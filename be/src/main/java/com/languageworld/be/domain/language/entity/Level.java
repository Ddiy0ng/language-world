package com.languageworld.be.domain.language.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "levels")
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60, nullable = false)
    private String level;

    @Column(name = "level_order", nullable = false)
    private int levelOrder;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;
}
