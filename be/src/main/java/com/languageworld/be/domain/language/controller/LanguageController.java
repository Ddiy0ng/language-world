package com.languageworld.be.domain.language.controller;

import com.languageworld.be.domain.language.dto.LanguageAndLevelResponseDto;
import com.languageworld.be.domain.language.service.LanguageService;
import com.languageworld.be.global.enumGroup.SuccessCode;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/language")
@RestController
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    @GetMapping
    public ResponseEntity<ApiResponse<LanguageAndLevelResponseDto>> findAllLanguageAndLevel() {

        LanguageAndLevelResponseDto languageAndLevelResponseDto = languageService.findAllLanguageAndLevel();

        return ResponseEntityUtil.success(SuccessCode.ALL_LANGUAGE_AND_LEVEL_FIND_SUCCESS, languageAndLevelResponseDto);
    }
}
