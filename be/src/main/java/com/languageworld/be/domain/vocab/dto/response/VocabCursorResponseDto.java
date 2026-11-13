package com.languageworld.be.domain.vocab.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record VocabCursorResponseDto (

        Long nextCursor,
        boolean hasNext,
        List<VocabResponseDto> vocabResponseDtoList
){

    public static VocabCursorResponseDto from(Long nextCursor, boolean hasNext, List<VocabResponseDto> vocabResponseDtoList) {

        return VocabCursorResponseDto.builder()
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .vocabResponseDtoList(vocabResponseDtoList)
                .build();
    }
}
