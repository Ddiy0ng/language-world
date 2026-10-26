package com.languageworld.be.domain.language.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record LanguageAndLevelResponseDto (

        List<LanguageResponseDto> languageResponseDtoList,
        List<LevelResponseDto> levelResponseDtoList
){

    public static LanguageAndLevelResponseDto of(List<LanguageResponseDto> languageResponseDtoList, List<LevelResponseDto> levelResponseDtoList) {

        LanguageAndLevelResponseDto languageAndLevelResponseDto = LanguageAndLevelResponseDto.builder()
                .languageResponseDtoList(languageResponseDtoList)
                .levelResponseDtoList(levelResponseDtoList)
                .build();

        return languageAndLevelResponseDto;
    }
}
