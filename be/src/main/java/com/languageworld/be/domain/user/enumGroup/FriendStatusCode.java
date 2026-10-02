package com.languageworld.be.domain.user.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FriendStatusCode {

    REQUESTED("요청"),
    ACCEPTED("수락");

    private final String statusMeaning;

}
