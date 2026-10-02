package com.languageworld.be.domain.language.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Table(name = "languages")
@Entity
@NoArgsConstructor
@Getter
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2, nullable = false)
    private String type;

    @Column(name = "supported_at", nullable = false)
    private LocalDateTime supportedAt;
}
