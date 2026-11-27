package com.languageworld.be.domain.game.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GameDrawRequestDto(

        // 50ms마다 묶어 보내는 획 하나는 보통 수백 자, 플레이에 지장 없게 넉넉히
        @NotBlank
        @Size(max = 20000)
        String drawData
) {
}