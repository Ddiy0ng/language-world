package com.languageworld.be.domain.user.dto.request;

import com.languageworld.be.domain.user.enumGroup.NationCode;

public record ChangeUserInfoRequestDto (

        String name,

        NationCode nation,

        String bio,

        Long motherTongueId
) {
}
