package com.languageworld.be.domain.vocab.controller;

import com.languageworld.be.domain.vocab.dto.response.VocabCursorResponseDto;
import com.languageworld.be.domain.vocab.service.VocabService;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import com.languageworld.be.global.response.enumGroup.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vocab")
public class VocabController {

    private final VocabService vocabService;

    // TODO 단어 검색
    @GetMapping
    public ResponseEntity<ApiResponse<VocabCursorResponseDto>> searchVocab(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                                 @RequestParam String language,
                                                                                 @RequestParam(required = false) String keyword,
                                                                                 @RequestParam(required = false) Long cursor) {

        VocabCursorResponseDto vocabCursorResponseDtoList = vocabService.searchVocab(customPrincipal, language, keyword, cursor);

        return ResponseEntityUtil.success(SuccessCode.VOCAB_FIND_SUCCESS, vocabCursorResponseDtoList);
    }
}
