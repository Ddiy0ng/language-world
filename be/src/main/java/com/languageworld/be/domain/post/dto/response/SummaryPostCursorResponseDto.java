package com.languageworld.be.domain.post.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record SummaryPostCursorResponseDto(

        Long nextCursor,
        boolean hasNext,
        List<PostSummaryResponseDto> postSummaryResponseDtoList
){

    public static SummaryPostCursorResponseDto from(Long nextCursor, boolean hasNext, List<PostSummaryResponseDto> postSummaryResponseDtoList) {

        SummaryPostCursorResponseDto summaryPostCursorResponseDto = SummaryPostCursorResponseDto.builder()
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .postSummaryResponseDtoList(postSummaryResponseDtoList)
                .build();

        return summaryPostCursorResponseDto;
    }
}
