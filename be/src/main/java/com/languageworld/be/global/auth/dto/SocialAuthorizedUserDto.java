package com.languageworld.be.global.auth.dto;

import com.languageworld.be.global.auth.enumGroup.SocialTypeCode;
import lombok.Builder;

@Builder
public record SocialAuthorizedUserDto(

        SocialTypeCode socialTypeCode,
        String sub,
        String profileImageUrl
) {
    public static SocialAuthorizedUserDto of(SocialTypeCode socialTypeCode, String sub, String profileImageUrl) {

        SocialAuthorizedUserDto socialAuthorizedUserDto = SocialAuthorizedUserDto.builder()
                .socialTypeCode(socialTypeCode)
                .sub(sub)
                .profileImageUrl(profileImageUrl)
                .build();

        return socialAuthorizedUserDto;
    }
}