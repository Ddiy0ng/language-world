package com.languageworld.be.domain.term.controller;

import com.languageworld.be.domain.term.dto.request.TermAgreeRequestDto;
import com.languageworld.be.domain.term.dto.request.TermCreateRequestDto;
import com.languageworld.be.domain.term.dto.response.AgreedTermResponseDto;
import com.languageworld.be.domain.term.dto.response.TermResponseDto;
import com.languageworld.be.domain.term.service.TermService;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.enumGroup.SuccessCode;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RequestMapping("/term")
@RestController
@RequiredArgsConstructor
public class TermController {

    private final TermService termService;

    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<Void>> registerTerm(@RequestPart("file") MultipartFile multipartFile,
                                                        @Valid @RequestPart("json") TermCreateRequestDto termCreateRequestDto) {

        termService.registerTerm(multipartFile, termCreateRequestDto);

        return ResponseEntityUtil.success(SuccessCode.TERM_CREATED);
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> agreeTermForAuthenticatedUser (@AuthenticationPrincipal CustomPrincipal customPrincipal, @Valid @RequestBody TermAgreeRequestDto termAgreeRequestDto) {

        termService.agreeTermForAuthenticatedUser(customPrincipal, termAgreeRequestDto);

        return ResponseEntityUtil.success(SuccessCode.AGREE_TERM_SUCCESS);
    }

    //TODO 약관 전체 조회(ADMIN)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<TermResponseDto>>> findTermList() {

        List<TermResponseDto> termResponseDtoList= termService.findTermList();

        return ResponseEntityUtil.success(SuccessCode.TERM_READ_SUCCESS, termResponseDtoList);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<TermResponseDto[]>> findActiveTerm() {

         TermResponseDto[] termResponseDtoArr = termService.findActiveTerm();

        return ResponseEntityUtil.success(SuccessCode.TERM_READ_SUCCESS, termResponseDtoArr);
    }

    @GetMapping("/agreed")
    public ResponseEntity<ApiResponse<List<AgreedTermResponseDto>>> findAgreedTermList(@AuthenticationPrincipal CustomPrincipal customPrincipal) {

        List<AgreedTermResponseDto> agreedTermList = termService.findAgreedTermList(customPrincipal);

        return ResponseEntityUtil.success(SuccessCode.AGREED_TERM_READ_SUCCESS, agreedTermList);
    }

    // TODO 비동의한 필수 약관 조회

    @PatchMapping("/admin/{termId}")
    public ResponseEntity<ApiResponse<Void>> activateTermStatus(@PathVariable Long termId) {

        termService.activateTermStatus(termId);

        return ResponseEntityUtil.success(SuccessCode.TERM_STATUS_UPDATE_SUCCESS);
    }

    @PatchMapping("/agreed/{agreedTermId}")
    public ResponseEntity<ApiResponse<Void>> disAgreeAgreedTerm(@AuthenticationPrincipal CustomPrincipal customPrincipal, @PathVariable Long agreedTermId) {

        termService.disAgreeAgreedTerm(customPrincipal, agreedTermId);

        return ResponseEntityUtil.success(SuccessCode.AGREED_TERM_DISAGREE_SUCCESS);
    }
}
