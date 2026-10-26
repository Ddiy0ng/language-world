package com.languageworld.be.domain.language.dto;

import com.languageworld.be.domain.language.entity.Level;
import lombok.Builder;

@Builder
public record LevelResponseDto (

        Long languageId,
        Long levelId,
        String levelName,
        int levelOrder

){

    public static LevelResponseDto from(Level level) {

        LevelResponseDto levelResponseDto = LevelResponseDto.builder()
                .languageId(level.getLanguage().getId())
                .levelId(level.getId())
                .levelName(level.getLevel())
                .levelOrder(level.getLevelOrder())
                .build();

        return levelResponseDto;
    }
}
