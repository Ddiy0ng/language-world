package com.languageworld.be.domain.user.controller;

import com.languageworld.be.domain.user.dto.response.BlockedUserResponseDto;
import com.languageworld.be.domain.user.service.BlockedUserService;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.enumGroup.SuccessCode;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/block")
@RestController
@RequiredArgsConstructor
public class BlockController {

    private final BlockedUserService blockedUserService;

    // TODO 사용자 차단(본인 스스로 불가, 차단 목록에 들어있는 사용자는 중복 차단 불가, 친구 여부 영향X): uuid
    @PostMapping("/{blockUuid}")
    public ResponseEntity<ApiResponse<Void>> blockUser(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                       @PathVariable UUID blockUuid) {

        blockedUserService.blockUser(customPrincipal, blockUuid);

        return ResponseEntityUtil.success(SuccessCode.USER_BLOCK_SUCCESS);
    }

    // TODO 내가 차단한 사용자 조회: authenticatedUserUuid
    @GetMapping
    public ResponseEntity<ApiResponse<List<BlockedUserResponseDto>>> findBlockedUserList(@AuthenticationPrincipal CustomPrincipal customPrincipal) {

        List<BlockedUserResponseDto> blockedUserResponseDtoList = blockedUserService.findBlockedUserList(customPrincipal);

        return ResponseEntityUtil.success(SuccessCode.ALL_BLOCKED_USER_FIND_SUCCESS, blockedUserResponseDtoList);
    }

    // TODO 사용자 차단 취소(차단 목록에 사용자 들어있는지 확인): blockId
    @DeleteMapping("/{blockId}")
    public ResponseEntity<ApiResponse<Void>> cancelBlock(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                         @PathVariable Long blockId) {

        blockedUserService.cancelBlock(customPrincipal, blockId);

        return ResponseEntityUtil.success(SuccessCode.BLOCK_CANCEL_SUCCESS);
    }
}
