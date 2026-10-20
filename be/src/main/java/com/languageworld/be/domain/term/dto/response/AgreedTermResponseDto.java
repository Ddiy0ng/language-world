package com.languageworld.be.domain.term.dto.response;

import com.languageworld.be.domain.term.entity.AgreedTerm;
import com.languageworld.be.domain.term.enumGroup.TermPurposeCode;
import lombok.Builder;

@Builder
public record AgreedTermResponseDto(

        Long id,
        TermPurposeCode purpose,
        String termPdfUrl,
        boolean isActive
) {

    public static AgreedTermResponseDto from(AgreedTerm agreedTerm) {

        AgreedTermResponseDto agreedTermResponseDto =
                AgreedTermResponseDto.builder()
                        .id(agreedTerm.getId())
                        .purpose(agreedTerm.getTerm().getPurpose())
                        .termPdfUrl(agreedTerm.getTerm().getTermPdfUrl())
                        .isActive(agreedTerm.getTerm().isActive())
                        .build();

        return agreedTermResponseDto;
    }
}
