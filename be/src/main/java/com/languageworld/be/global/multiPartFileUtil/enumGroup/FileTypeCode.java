package com.languageworld.be.global.multiPartFileUtil.enumGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileTypeCode {

    PDF("pdf", "application/pdf"),
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png");

    private final String extension;
    private final String contentType;
}
