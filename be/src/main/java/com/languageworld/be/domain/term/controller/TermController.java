package com.languageworld.be.domain.term.controller;

import com.languageworld.be.domain.term.dto.response.AgreedTermResponseDto;
import com.languageworld.be.domain.term.dto.response.TermResponseDto;
import com.languageworld.be.domain.term.dto.request.TermRequestDto;
import com.languageworld.be.domain.term.service.TermService;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.enumGroup.SuccessCode;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

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
            description = "약관 목적, 버전 및 PDF 파일을 등록합니다.",
            security = @SecurityRequirement(name = "BearerAuth"),
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            encoding = {
                                    @Encoding(
                                            name = "json",
                                            contentType = MediaType.APPLICATION_JSON_VALUE
                                    ),
                                    @Encoding(
                                            name = "file",
                                            contentType = MediaType.APPLICATION_PDF_VALUE
                                    )
                            }
                    )
            )
    )
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> createTerm(

            @AuthenticationPrincipal CustomPrincipal customPrincipal,

            @Parameter(
                    description = "약관 PDF 파일",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PDF_VALUE,
                            schema = @Schema(
                                    type = "string",
                                    format = "binary"
                            )
                    )
            )
            @RequestPart("file")
            MultipartFile multipartFile,

            @Parameter(
                    description = "약관 생성 요청 DTO",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = TermRequestDto.TermCreate.class
                            )
                    )
            )
            @Valid
            @RequestPart("json")
            TermRequestDto.TermCreate termRequestDto
    ) {

        termService.createTerm(
                customPrincipal,
                multipartFile,
                termRequestDto
        );

        return ResponseEntityUtil.success(SuccessCode.TERM_CREATED);
    }

    // TODO 약관 조회(ADMIN): active인 서비스 이용, 개인정보 수집 약관 조회
    @GetMapping()
    public ResponseEntity<ApiResponse<List<TermResponseDto>>> getActiveTerm() {

         List<TermResponseDto> termResponseDtoList = termService.getActiveTerm();

        return ResponseEntityUtil.success(SuccessCode.TERM_READ_SUCCESS, termResponseDtoList);
    }

    // TODO 약관 조회(USER): 동의한 약관 확인
    @GetMapping("/agreed")
    public ResponseEntity<ApiResponse<List<AgreedTermResponseDto>>> getAgreedTermList(
            @AuthenticationPrincipal CustomPrincipal customPrincipal
    ) {

        List<AgreedTermResponseDto> agreedTermList = termService.getAgreedTermList(customPrincipal);

        return ResponseEntityUtil.success(SuccessCode.AGREED_TERM_READ_SUCCESS, agreedTermList);
    }

    // TODO 약관 상태 변경(ADMIN): Active/Inactive
    @PatchMapping("/{termId}")
    public ResponseEntity<ApiResponse<Void>> updateTermStatus(
            @AuthenticationPrincipal CustomPrincipal customPrincipal,
            @PathVariable Long termId,
            TermRequestDto.TermStatusUpdate termRequestDto
    ) {

        termService.updateTermStatus(customPrincipal, termId, termRequestDto);

        return ResponseEntityUtil.success(SuccessCode.TERM_STATUS_UPDATE_SUCCESS);
    }

    // TODO 약관 동의 여부 변경(USER): 동의 철회
    @PatchMapping("/agreed/{termId}")
    public ResponseEntity<ApiResponse<Void>> updateAgreedTermIsAgreed(
            @PathVariable Long agreedTermId,
            TermRequestDto.AgreedTermIsAgreedUpdate termRequestDto
    ) {

        termService.updateAgreedTermIsAgreed(agreedTermId, termRequestDto);

        return ResponseEntityUtil.success(SuccessCode.AGREED_TERM_IS_AGREED_UPDATE_SUCCESS);
    }

    // TODO 약관 삭제
    @DeleteMapping("/{termId}")
    public ResponseEntity<ApiResponse<Void>> deleteTerm(
            @AuthenticationPrincipal CustomPrincipal customPrincipal,
            @PathVariable Long termId
    ) {

        termService.deleteTerm(customPrincipal, termId);

        return ResponseEntityUtil.success(SuccessCode.TERM_DELETE_SUCCESS);
    }
}
