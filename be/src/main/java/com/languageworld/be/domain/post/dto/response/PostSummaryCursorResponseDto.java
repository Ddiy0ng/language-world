package com.languageworld.be.domain.post.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PostSummaryCursorResponseDto(

        Long nextCursor,
        boolean hasNext,
        List<PostSummaryResponseDto> postSummaryResponseDtoList
){

    public static PostSummaryCursorResponseDto from(Long nextCursor, boolean hasNext, List<PostSummaryResponseDto> postSummaryResponseDtoList) {

        PostSummaryCursorResponseDto postSummaryCursorResponseDto = PostSummaryCursorResponseDto.builder()
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .postSummaryResponseDtoList(postSummaryResponseDtoList)
                .build();

        return postSummaryCursorResponseDto;
    }
}
