package com.languageworld.be.domain.language.dto;

import com.languageworld.be.domain.language.entity.Language;
import lombok.Builder;

@Builder
public record LanguageResponseDto (

        Long languageId,
        String languageType
){

    public static LanguageResponseDto from(Language language) {

        LanguageResponseDto languageResponseDto = LanguageResponseDto.builder()
                .languageId(language.getId())
                .languageType(language.getType())
                .build();

        return languageResponseDto;
    }
}
