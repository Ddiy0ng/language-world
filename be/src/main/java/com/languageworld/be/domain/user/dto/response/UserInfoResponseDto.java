package com.languageworld.be.domain.user.dto.response;

import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.enumGroup.NationCode;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserInfoResponseDto (

    UUID userUuid,

    //프로필 이미지, 이름,  국적, 모국어, 학습어 + 레벨, 바이오
    String pdfImageUrl,

    String name,

    NationCode nation,

    String motherTongue,

    String learningLanguage,

    String level,

    String bio
) {

    public static UserInfoResponseDto from(User user) {

        UserInfoResponseDto userInfoResponseDto = UserInfoResponseDto.builder()
                .userUuid(user.getUuid())
                .pdfImageUrl(user.getProfileImageUrl())
                .name(user.getName())
                .nation(user.getNation())
                .motherTongue(user.getMotherTongue().getType())
                .learningLanguage(user.getLearningLanguage().getType())
                .level(user.getLearningLanguageLevel().getLevel())
                .bio(user.getBio())
                .build();

        return userInfoResponseDto;
    }
}
