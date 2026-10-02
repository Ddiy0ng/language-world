package com.languageworld.be.domain.user.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NationCode {

    KR("Republic of Korea"),
    US("United States of America");

    private final String fullName;
}
