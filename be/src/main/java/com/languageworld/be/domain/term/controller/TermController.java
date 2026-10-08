package com.languageworld.be.domain.term.controller;

import com.languageworld.be.domain.term.dto.TermCreateRequestDto;
import com.languageworld.be.domain.term.service.TermService;
import com.languageworld.be.global.enumGroup.SuccessCode;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(
        name = "TERM",
        description = "약관 관리 API 입니다."
)
@RequestMapping("/term")
@RestController
@RequiredArgsConstructor
public class TermController {

    private final TermService termService;

    @Operation(
            summary = "약관 PDF 등록",
            description = "약관, 목적, 버전 및 PDF 파일을 등록합니다."
    )
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> createTerm(@RequestPart("file") MultipartFile multipartFile, @Valid @RequestPart("json") TermCreateRequestDto termCreateRequestDto) {

        termService.createTerm(multipartFile, termCreateRequestDto);

        return ResponseEntityUtil.success(SuccessCode.TERM_CREATED);
    }
}
