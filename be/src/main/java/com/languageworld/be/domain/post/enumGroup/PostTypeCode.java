package com.languageworld.be.domain.post.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PostTypeCode {

    DAILY("일상글"),
    HELP("질문글");

    private final String typeMeaning;
}
