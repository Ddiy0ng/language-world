package com.languageworld.be.domain.user.controller;

import com.languageworld.be.domain.user.dto.response.FriendRequestResponseDto;
import com.languageworld.be.domain.user.service.FriendService;
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

@RequestMapping("/friend")
@RestController
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // TODO 친구 추가
    @PostMapping("/{friendUuid}")
    public ResponseEntity<ApiResponse<Void>> addFriend(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                       @PathVariable UUID friendUuid) {

        // 사용자 존재 확인, 친구 여부 확인, 추가
        friendService.addFriend(customPrincipal, friendUuid);

        return ResponseEntityUtil.success(SuccessCode.FRIEND_ADD_SUCCESS);
    }

    // TODO 내가 보낸 친구 요청 및 거절 내역 조회
    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<List<FriendRequestResponseDto>>> findSentFriendRequest(@AuthenticationPrincipal CustomPrincipal customPrincipal) {

        List<FriendRequestResponseDto> friendResponseDtoList = friendService.findSentFriendRequestList(customPrincipal);

        return ResponseEntityUtil.success(SuccessCode.SENT_FRIEND_REQUEST_FIND_SUCCESS, friendResponseDtoList);
    }

    // TODO 내가 받은 친구 요청 내역 조회
    @GetMapping("/received")
    public ResponseEntity<ApiResponse<List<FriendRequestResponseDto>>> findReceivedFriendRequest(@AuthenticationPrincipal CustomPrincipal customPrincipal) {

        List<FriendRequestResponseDto> friendResponseDtoList = friendService.findReceivedFriendRequestList(customPrincipal);

        return ResponseEntityUtil.success(SuccessCode.RECEIVED_FRIEND_REQUEST_FIND_SUCCESS, friendResponseDtoList);
    }

    // TODO 친구 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<FriendRequestResponseDto>>> searchFriendList(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                                      @RequestParam(required = false) String keyword) {

        List<FriendRequestResponseDto> friendResponseDtoList = friendService.searchFriendList(customPrincipal, keyword);

        return ResponseEntityUtil.success(SuccessCode.ALL_FRIEND_FIND_SUCCESS, friendResponseDtoList);
    }

    // TODO 수락
    @PatchMapping("/request/{friendId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptFriendRequest(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                 @PathVariable Long friendId) {

        friendService.acceptFriendRequest(customPrincipal, friendId);

        return ResponseEntityUtil.success(SuccessCode.FRIEND_REQUEST_ACCEPT_SUCCESS);
    }

    // TODO 거절
    @PatchMapping("/request/{friendId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectFriendRequest(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                 @PathVariable Long friendId) {

        friendService.rejectFriendRequest(customPrincipal, friendId);

        return ResponseEntityUtil.success(SuccessCode.FRIEND_REQUEST_REJECT_SUCCESS);
    }

    // TODO 친구 요청 취소
    @DeleteMapping("/request/{friendId}")
    public ResponseEntity<ApiResponse<Void>> cancelFriendRequest(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                 @PathVariable Long friendId) {

        friendService.cancelFriendRequest(customPrincipal, friendId);

        return ResponseEntityUtil.success(SuccessCode.FRIEND_REQUEST_CANCEL_SUCCESS);
    }

    // TODO 친구 삭제
    @DeleteMapping("/{friendUuid}")
    public ResponseEntity<ApiResponse<Void>> removeFriend(@AuthenticationPrincipal CustomPrincipal customPrincipal, @PathVariable UUID friendUuid) {

        friendService.removeFriend(customPrincipal, friendUuid);

        return ResponseEntityUtil.success(SuccessCode.FRIEND_REMOVE_SUCCESS);
    }
}
