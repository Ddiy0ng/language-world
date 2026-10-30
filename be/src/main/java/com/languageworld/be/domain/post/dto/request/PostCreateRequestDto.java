package com.languageworld.be.domain.post.dto.request;

import com.languageworld.be.domain.post.enumGroup.PostTypeCode;
import jakarta.validation.constraints.NotBlank;

public record PostCreateRequestDto(

        @NotBlank
        PostTypeCode postTypeCode,

        @NotBlank
        String title,

        @NotBlank
        String contentText
) {
}
