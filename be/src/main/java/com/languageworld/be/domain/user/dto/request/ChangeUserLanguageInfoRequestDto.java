package com.languageworld.be.domain.user.dto.request;

public record ChangeUserLanguageInfoRequestDto (

        Long learningLanguageId,

        Long learningLanguageLevelId
){
}
