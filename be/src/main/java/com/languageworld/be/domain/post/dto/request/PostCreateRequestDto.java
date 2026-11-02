package com.languageworld.be.domain.post.dto.request;

import com.languageworld.be.domain.post.enumGroup.PostTypeCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostCreateRequestDto(

        @NotNull
        PostTypeCode postTypeCode,

        @NotBlank
        String title,

        @NotBlank
        String contentText
) {
}
