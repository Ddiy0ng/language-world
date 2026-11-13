package com.languageworld.be.domain.vocab.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record SavedVocabCursorResponseDto(

        Long nextCursor,
        boolean hasNext,
        List<SavedVocabResponseDto> savedVocabResponseDtoList
) {

    public static SavedVocabCursorResponseDto from(Long nextCursor, boolean hasNext, List<SavedVocabResponseDto> savedVocabResponseDtoList) {

        return SavedVocabCursorResponseDto.builder()
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .savedVocabResponseDtoList(savedVocabResponseDtoList)
                .build();
    }
}
