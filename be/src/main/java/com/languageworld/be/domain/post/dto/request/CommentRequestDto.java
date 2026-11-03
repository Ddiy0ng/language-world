package com.languageworld.be.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentRequestDto(

        @NotBlank
        String content
) {
}
