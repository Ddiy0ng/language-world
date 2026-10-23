package com.languageworld.be.domain.user.dto.response;

import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.enumGroup.NationCode;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserSummaryResponseDto (

        UUID userUuid,

        // 프로필 이미지, 이름, 국적, 학습어 + 레벨
        String pdfImageUrl,

        String name,

        NationCode nation,

        String learningLanguage,

        String level
) {

    public static UserSummaryResponseDto from(User user) {

        UserSummaryResponseDto userSummaryResponseDto = UserSummaryResponseDto.builder()
                .userUuid(user.getUuid())
                .pdfImageUrl(user.getProfileImageUrl())
                .name(user.getName())
                .nation(user.getNation())
                .learningLanguage(user.getLearningLanguage().getType())
                .level(user.getLearningLanguageLevel().getLevel())
                .build();

        return userSummaryResponseDto;
    }
}
