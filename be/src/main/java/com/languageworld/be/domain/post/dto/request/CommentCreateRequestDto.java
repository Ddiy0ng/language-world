package com.languageworld.be.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequestDto(

        @NotBlank
        String content
) {
}
