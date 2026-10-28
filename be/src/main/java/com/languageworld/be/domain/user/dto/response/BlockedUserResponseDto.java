package com.languageworld.be.domain.user.dto.response;

import com.languageworld.be.domain.user.entity.BlockedUser;
import lombok.Builder;

@Builder
public record BlockedUserResponseDto(

        Long blockId,
        UserSummaryResponseDto blockedUser
) {

    public static BlockedUserResponseDto from(BlockedUser blockedUser) {

        BlockedUserResponseDto blockedUserResponseDto = BlockedUserResponseDto.builder()
                .blockId(blockedUser.getId())
                .blockedUser(UserSummaryResponseDto.from(blockedUser.getBlockedUser()))
                .build();

        return blockedUserResponseDto;
    }
}
