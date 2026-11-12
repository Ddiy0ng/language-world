package com.languageworld.be.domain.chat.controller;

import com.languageworld.be.domain.chat.dto.response.*;
import com.languageworld.be.domain.chat.service.ChatService;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import com.languageworld.be.global.response.enumGroup.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/{userUuid}")
    public ResponseEntity<ApiResponse<FriendChatroomEnterResponseDto>> enterFriendChatroom(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                                           @PathVariable UUID userUuid) {

        // 친구창 통해 채팅방 입장(생성): dm
        FriendChatroomEnterResponseDto friendChatroomEnterResponseDto = chatService.enterFriendChatroom(customPrincipal, userUuid);

        return ResponseEntityUtil.success(SuccessCode.CHATROOM_CREATE_SUCCESS, friendChatroomEnterResponseDto);
    }

    @GetMapping("/{chatroomId}")
    public ResponseEntity<ApiResponse<ChatCursorResponseDto>> findChatList(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                           @PathVariable Long chatroomId,
                                                                           @RequestParam(required = false) Long cursor) {

        ChatCursorResponseDto chatCursorResponseDto = chatService.findChatList(customPrincipal, chatroomId, cursor);

        return ResponseEntityUtil.success(SuccessCode.CHAT_LIST_FIND_SUCCESS, chatCursorResponseDto);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ChatroomCursorResponseDto>> findChatroomList(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                                   @RequestParam(required = false) String keyword,
                                                                                   @RequestParam(required = false) Long cursor) {

        ChatroomCursorResponseDto chatroomCursorResponseDto = chatService.findChatroomList(customPrincipal, keyword, cursor);

        return ResponseEntityUtil.success(SuccessCode.CHATROOM_LIST_FIND_SUCCESS, chatroomCursorResponseDto);
    }

    @DeleteMapping("/{chatroomId}")
    public ResponseEntity<ApiResponse<Void>> quitChatroom(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                          @PathVariable Long chatroomId) {

        chatService.quitChatroom(customPrincipal, chatroomId);

        return ResponseEntityUtil.success(SuccessCode.CHATROOM_QUIT_SUCCESS);
    }

}
