package com.languageworld.be.domain.user.dto.request;

public record ChangeUserPasswordRequestDto(

        String oldPassword,
        String newPassword
) {
}
