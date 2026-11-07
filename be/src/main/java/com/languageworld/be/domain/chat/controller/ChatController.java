package com.languageworld.be.domain.chat.controller;

import com.languageworld.be.domain.chat.dto.request.ChatSaveRequestDto;
import com.languageworld.be.domain.chat.dto.response.ChatResponseDto;
import com.languageworld.be.domain.chat.dto.response.FriendChatroomEnterResponseDto;
import com.languageworld.be.domain.chat.dto.response.ChatroomResponseDto;
import com.languageworld.be.domain.chat.service.ChatService;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.response.ApiResponse;
import com.languageworld.be.global.response.ResponseEntityUtil;
import com.languageworld.be.global.response.enumGroup.SuccessCode;
import jakarta.validation.Valid;
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

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> saveChat(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                      @Valid @RequestBody ChatSaveRequestDto chatSaveRequestDto) {

        chatService.saveChat(customPrincipal, chatSaveRequestDto);

        return ResponseEntityUtil.success(SuccessCode.CHAT_SAVE_SUCCESS);
    }

    @GetMapping("/{chatroomId}")
    public ResponseEntity<ApiResponse<List<ChatResponseDto>>> findChatList(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                                           @PathVariable Long chatroomId) {

        List<ChatResponseDto> chatResponseDtoList = chatService.findChatList(customPrincipal, chatroomId);

        return ResponseEntityUtil.success(SuccessCode.CHAT_LIST_FIND_SUCCESS, chatResponseDtoList);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatroomResponseDto>>> findChatroomList(@AuthenticationPrincipal CustomPrincipal customPrincipal) {

        List<ChatroomResponseDto> chatroomResponseDtoList = chatService.findChatroomList(customPrincipal);

        return ResponseEntityUtil.success(SuccessCode.CHATROOM_LIST_FIND_SUCCESS, chatroomResponseDtoList);
    }

    @DeleteMapping("/{chatroomId}")
    public ResponseEntity<ApiResponse<Void>> quitChatroom(@AuthenticationPrincipal CustomPrincipal customPrincipal,
                                                          @PathVariable Long chatRoomId) {

        chatService.quitChatroom(customPrincipal, chatRoomId);

        return ResponseEntityUtil.success(SuccessCode.CHATROOM_QUIT_SUCCESS);
    }

}
