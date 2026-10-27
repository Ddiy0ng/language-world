package com.languageworld.be.domain.user.dto.response;

import com.languageworld.be.domain.user.entity.Friend;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.enumGroup.FriendStatusCode;
import lombok.Builder;
import java.util.UUID;

@Builder
public record FriendRequestResponseDto(

        Long friendRequestId,
        UserSummaryResponseDto friend,
        FriendStatusCode friendStatusCode
) {

    public static FriendRequestResponseDto fromSent(Friend friend) {

        FriendRequestResponseDto friendRequestResponseDto = FriendRequestResponseDto.builder()
                .friendRequestId(friend.getId())
                .friend(UserSummaryResponseDto.from(friend.getFriend()))
                .friendStatusCode(friend.getFriendStatus())
                .build();

        return friendRequestResponseDto;
    }

    public static FriendRequestResponseDto fromReceived(Friend friend) {

        FriendRequestResponseDto friendRequestResponseDto = FriendRequestResponseDto.builder()
                .friendRequestId(friend.getId())
                .friend(UserSummaryResponseDto.from(friend.getUser()))
                .friendStatusCode(friend.getFriendStatus())
                .build();

        return friendRequestResponseDto;
    }

    public static FriendRequestResponseDto from(UUID authenticatedUserUuid, Friend friend) {

        User taretUser = friend.getUser().getUuid().equals(authenticatedUserUuid) ? friend.getFriend() : friend.getUser();

        FriendRequestResponseDto friendRequestResponseDto = FriendRequestResponseDto.builder()
                .friendRequestId(friend.getId())
                .friend(UserSummaryResponseDto.from(taretUser))
                .friendStatusCode(friend.getFriendStatus())
                .build();

        return friendRequestResponseDto;
    }
}
