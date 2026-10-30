package com.languageworld.be.domain.post.dto.request;

import java.util.List;

public record PostUpdateRequestDto(

        String title,

        String contentText,

        List<PostImageStatusOrderDto> postImageStatusOrderDtoList
) {
}
