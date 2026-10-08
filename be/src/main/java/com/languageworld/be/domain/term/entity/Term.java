package com.languageworld.be.domain.term.entity;

import com.languageworld.be.domain.term.dto.TermCreateRequestDto;
import com.languageworld.be.domain.term.enumGroup.TermPurposeCode;
import com.languageworld.be.global.baseEntity.CreatableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Table(name = "terms")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity

@Getter
public class Term extends CreatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TermPurposeCode purpose;

    @Column(name = "content_pdf_url", nullable = false)
    private String termPdfUrl;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private String version;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    public static Term of(TermCreateRequestDto termCreateRequestDto, String fileUploadedPath, String version) {

        Term term = Term.builder()
                .purpose(termCreateRequestDto.purpose())
                .termPdfUrl(fileUploadedPath)
                .isActive(false)
                .version(version)
                .build();

        return term;
    }
}
