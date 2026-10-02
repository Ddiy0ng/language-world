package com.languageworld.be.domain.term.entity;

import com.languageworld.be.domain.term.enumGroup.TermPurposeCode;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Table(name = "terms")
@Entity
@Getter
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TermPurposeCode purpose;

    @Column(name = "content_pdf_url", nullable = false)
    private String contentPdfUrl;

    @Column(name = "is_necessary", nullable = false)
    private boolean isNecessary;

    @Column(length = 20, nullable = false)
    private String version;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
