package com.languageworld.be.domain.vocab.controller;

import com.languageworld.be.domain.vocab.dto.request.SavedVocabDeleteRequestDto;
import com.languageworld.be.domain.vocab.dto.request.SavedVocabSaveRequestDto;
import com.languageworld.be.domain.vocab.dto.request.SavedVocabUpdateRequestDto;
import com.languageworld.be.domain.vocab.dto.response.SavedVocabCursorResponseDto;
import com.languageworld.be.domain.vocab.service.SavedVocabService;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import com.languageworld.be.global.response.enumGroup.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/savedVocab")
public class SavedVocabController {

    private final SavedVocabService savedVocabService;

    // TODO 단어 저장 + 메모 저장
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> saveSavedVocab(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                            @Valid @RequestBody SavedVocabSaveRequestDto savedVocabSaveRequestDto) {

        savedVocabService.saveSavedVocab(customPrincipal, savedVocabSaveRequestDto);

        return ResponseEntityUtil.success(SuccessCode.SAVED_VOCAB_SAVE_SUCCESS);
    }

    // TODO 저장된 단어 조회(검색 지원)
    @GetMapping
    public ResponseEntity<ApiResponse<SavedVocabCursorResponseDto>> searchSavedVocabList(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                                         @RequestParam(required = false) String language,
                                                                                         @RequestParam(required = false) String keyword,
                                                                                         @RequestParam(required = false) Long cursor){

        SavedVocabCursorResponseDto savedVocabCursorResponseDto = savedVocabService.searchSavedVocabList(customPrincipal, language, keyword, cursor);

        return ResponseEntityUtil.success(SuccessCode.SAVED_VOCAB_SEARCH_SUCCESS, savedVocabCursorResponseDto);
    }

    // TODO 단어의 메모 수정
    @PatchMapping("/{savedVocabId}")
    public ResponseEntity<ApiResponse<Void>> changeSavedVocabMemo(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                  @PathVariable Long savedVocabId,
                                                                  @Valid @RequestBody SavedVocabUpdateRequestDto savedVocabUpdateRequestDto){

        savedVocabService.changeSavedVocabMemo(customPrincipal, savedVocabId, savedVocabUpdateRequestDto);

        return ResponseEntityUtil.success(SuccessCode.SAVED_VOCAB_MEMO_CHANGE_SUCCESS);
    }

    // TODO 저장된 단어 삭제(단건)
    @DeleteMapping("/{savedVocabId}")
    public ResponseEntity<ApiResponse<Void>> removeSavedVocab(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                              @PathVariable Long savedVocabId){

        savedVocabService.removeSavedVocab(customPrincipal, savedVocabId);

        return ResponseEntityUtil.success(SuccessCode.SAVED_VOCAB_DELETE_SUCCESS);
    }

    // TODO 저장된 단어 여러 건 동시 삭제
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeSavedVocabList(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                  @Valid @RequestBody SavedVocabDeleteRequestDto savedVocabDeleteRequestDto){

        savedVocabService.removeSavedVocabList(customPrincipal, savedVocabDeleteRequestDto);

        return ResponseEntityUtil.success(SuccessCode.SAVED_VOCAB_DELETE_SUCCESS);
    }
}
