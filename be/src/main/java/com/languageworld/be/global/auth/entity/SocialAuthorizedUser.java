package com.languageworld.be.global.auth.entity;

import com.languageworld.be.global.auth.enumGroup.SocialTypeCode;
import lombok.Builder;

@Builder
public record SocialAuthorizedUser(

        String sub,
        String profileImageUrl
) {
    public static SocialAuthorizedUser of(String sub, String profileImageUrl) {

        SocialAuthorizedUser socialAuthorizedUser = SocialAuthorizedUser.builder()
                .sub(sub)
                .profileImageUrl(profileImageUrl)
                .build();

        return socialAuthorizedUser;
    }
}