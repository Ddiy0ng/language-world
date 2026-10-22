package com.languageworld.be.domain.term.controller;

import com.languageworld.be.domain.term.dto.request.TermAgreeRequestDto;
import com.languageworld.be.domain.term.dto.request.TermCreateRequestDto;
import com.languageworld.be.domain.term.dto.request.TermActivenessUpdateRequestDto;
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
    public ResponseEntity<ApiResponse<Void>> createTerm(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                        @RequestPart("file") MultipartFile multipartFile,
                                                        @Valid @RequestPart("json") TermCreateRequestDto termCreateRequestDto) {

        termService.createTerm(multipartFile, termCreateRequestDto);

        return ResponseEntityUtil.success(SuccessCode.TERM_CREATED);
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> agreeTerm(@AuthenticationPrincipal CustomPrincipal customPrincipal, TermAgreeRequestDto termAgreeRequestDto) {

        termService.agreeTerm(customPrincipal, termAgreeRequestDto);

        return ResponseEntityUtil.success(SuccessCode.AGREE_TERM_SUCCESS);
    }

    //TODO 약관 전체 조회(ADMIN)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<TermResponseDto>>> getTermList() {

        List<TermResponseDto> termResponseDtoList= termService.getAllTerm();

        return ResponseEntityUtil.success(SuccessCode.TERM_READ_SUCCESS, termResponseDtoList);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<TermResponseDto>>> getActiveTerm() {

         List<TermResponseDto> termResponseDtoList = termService.getActiveTerm();

        return ResponseEntityUtil.success(SuccessCode.TERM_READ_SUCCESS, termResponseDtoList);
    }

    @GetMapping("/agreed")
    public ResponseEntity<ApiResponse<List<AgreedTermResponseDto>>> getAgreedTermList(@AuthenticationPrincipal CustomPrincipal customPrincipal) {

        List<AgreedTermResponseDto> agreedTermList = termService.getAgreedTermList(customPrincipal);

        return ResponseEntityUtil.success(SuccessCode.AGREED_TERM_READ_SUCCESS, agreedTermList);
    }

    @PatchMapping("/admin/{termId}")
    public ResponseEntity<ApiResponse<Void>> updateTermStatus(@PathVariable Long termId,
                                                              @Valid @RequestBody TermActivenessUpdateRequestDto termActivenessUpdateRequestDto) {

        termService.updateTermActiveness(termId, termActivenessUpdateRequestDto);

        return ResponseEntityUtil.success(SuccessCode.TERM_STATUS_UPDATE_SUCCESS);
    }

    @PatchMapping("/agreed/{agreedTermId}")
    public ResponseEntity<ApiResponse<Void>> disAgreeAgreedTerm(@AuthenticationPrincipal CustomPrincipal customPrincipal, @PathVariable Long agreedTermId) {

        termService.disAgreeAgreedTerm(customPrincipal, agreedTermId);

        return ResponseEntityUtil.success(SuccessCode.AGREED_TERM_DISAGREE_SUCCESS);
    }
}
