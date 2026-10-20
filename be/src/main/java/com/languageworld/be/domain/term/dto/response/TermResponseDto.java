package com.languageworld.be.domain.term.dto.response;

import com.languageworld.be.domain.term.entity.Term;
import com.languageworld.be.domain.term.enumGroup.TermPurposeCode;
import lombok.Builder;

@Builder
public record TermResponseDto(

        Long id,
        TermPurposeCode purpose,
        String termPdfUrl,
        boolean isActive
) {

    public static TermResponseDto from(Term term) {

         TermResponseDto termResponseDto =
                 TermResponseDto.builder()
                         .id(term.getId())
                         .purpose(term.getPurpose())
                         .termPdfUrl(term.getTermPdfUrl())
                         .isActive(term.isActive())
                         .build();

         return termResponseDto;
    }
}
