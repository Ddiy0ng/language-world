package com.languageworld.be.domain.chat.service;

import com.languageworld.be.domain.chat.dto.response.*;
import com.languageworld.be.domain.chat.entity.Chat;
import com.languageworld.be.domain.chat.entity.Chatroom;
import com.languageworld.be.domain.chat.entity.ChatroomUser;
import com.languageworld.be.domain.chat.repository.ChatRepository;
import com.languageworld.be.domain.chat.repository.ChatroomRepository;
import com.languageworld.be.domain.chat.repository.ChatroomUserRepository;
import com.languageworld.be.domain.user.dto.response.UserSummaryResponseDto;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.service.BlockedUserUtil;
import com.languageworld.be.domain.user.service.FriendUtil;
import com.languageworld.be.domain.user.service.UserUtil;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatroomRepository chatroomRepository;
    private final ChatroomUserRepository chatroomUserRepository;
    private final UserUtil userUtil;
    private final ChatroomUserUtil chatroomUserUtil;
    private final ChatroomUtil chatroomUtil;
    private final FriendUtil friendUtil;
    private final BlockedUserUtil blockedUserUtil;

    // 친구창 통해 채팅방 입장(dm)
    @Transactional
    public FriendChatroomEnterResponseDto enterFriendChatroom(CustomPrincipal customPrincipal, UUID userUuid) {

        UUID authenticatedUserUuid = customPrincipal.uuid();

        // 자기 자신과 채팅 불가
        if(authenticatedUserUuid.equals(userUuid)) {

            CustomLogger.warn(
                    LogEventCode.CHAT,
                    "FAIL",
                    LogEventReasonCode.CANT_CHAT_WITH_SELF,
                    LogEventReasonCode.CANT_CHAT_WITH_SELF.getMessage(),
                    authenticatedUserUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.CANT_CHAT_WITH_SELF);
        }

        // 상대 탈퇴 여부
        userUtil.deletedUserCheckByUserUuid(userUuid);

        // 수락된 친구인지
        friendUtil.acceptedFriendCheck(authenticatedUserUuid, userUuid);

        // 양방향 차단 여부
        blockedUserUtil.blockedEachOtherCheck(authenticatedUserUuid, userUuid);

        // 두 사람의 개인방 조회(퇴장 여부 무관)
        String personalKey = Chatroom.createPersonalKey(authenticatedUserUuid, userUuid);
        Optional<Chatroom> optionalChatroom = chatroomRepository.findByPersonalKey(personalKey);

        if(optionalChatroom.isPresent()) {

            Chatroom chatroom = optionalChatroom.get();

            // 본인 이전 퇴장 여부 확인, 재입장(quitAt을 null로)
            ChatroomUser chatroomUser = chatroomUserUtil.getChatroomUserByChatroomIdAndUserUuid(chatroom.getId(), authenticatedUserUuid);
            if(chatroomUser.getQuitAt() != null)
                chatroomUser.reEnterChatroom();

            // TODO 3-3: 상대방이 퇴장 상태일 때 처리(정책 결정 후)

            return FriendChatroomEnterResponseDto.from(chatroom.getId());
        }

        User authenticatedUser = userUtil.getUserByUuid(authenticatedUserUuid);
        User friend = userUtil.getUserByUuid(userUuid);

        // 동시 요청이면 여기서 personal_key unique 위반 발생
        Chatroom chatroom = Chatroom.ofPersonal(authenticatedUser, friend);
        chatroomRepository.save(chatroom);

        chatroomUserRepository.save(ChatroomUser.of(authenticatedUser, chatroom));
        chatroomUserRepository.save(ChatroomUser.of(friend, chatroom));

        return FriendChatroomEnterResponseDto.from(chatroom.getId());
    }

    @Transactional
    public ChatCursorResponseDto findChatList(CustomPrincipal customPrincipal, Long chatroomId, Long cursor) {

        UUID userUuid = customPrincipal.uuid();

        // 채팅방 멤버 여부 확인
        chatroomUserUtil.getChatroomUserByChatroomIdAndUserUuid(chatroomId, userUuid);

        // 채팅방 에시지 히스토리 조회
        Slice<Chat> chatSlice = findChat(chatroomId, userUuid, cursor);

        List<ChatResponseDto> chatResponseDtoList = chatSlice.getContent().stream()
                .map(chat -> ChatResponseDto.from(chat, UserSummaryResponseDto.from(chat.getSender()), chat.getChatroom().getId()))
                .toList();

        boolean hasNext = chatSlice.hasNext();

        Long nextCursor = null;
        // 다음 있을 때 커서 등록
        if(hasNext && !chatSlice.isEmpty()) {

            // 마지막 요소의 인덱스를 다음 커서로
            nextCursor = chatSlice.getContent().get(chatSlice.getNumberOfElements() - 1).getId();
        }

        return ChatCursorResponseDto.from(nextCursor, hasNext, chatResponseDtoList);
    }

    @Transactional
    public ChatroomCursorResponseDto findChatroomList(CustomPrincipal customPrincipal, String keyword, Long cursor) {

        UUID userUuid = customPrincipal.uuid();

        Slice<Chatroom> chatroomSlice = findChatroom(userUuid, keyword, cursor);

        List<ChatroomResponseDto> chatroomResponseDtoList = chatroomSlice.getContent().stream()
                .map(ChatroomResponseDto::from)
                .toList();

        boolean hasNext = chatroomSlice.hasNext();

        Long nextCursor = null;
        // 다음 있을 때 커서 등록
        if(hasNext && !chatroomSlice.isEmpty()) {

            // 마지막 요소의 인덱스를 다음 커서로
            nextCursor = chatroomSlice.getContent().get(chatroomSlice.getNumberOfElements() - 1).getId();
        }

        return ChatroomCursorResponseDto.from(nextCursor, hasNext, chatroomResponseDtoList);
    }

    @Transactional
    public void quitChatroom(CustomPrincipal customPrincipal, Long chatroomId) {

        UUID userUuid = customPrincipal.uuid();

        // 멤버
        ChatroomUser chatroomUser = chatroomUserUtil.getChatroomUserByChatroomIdAndUserUuid(chatroomId, userUuid);

        // 채팅방 조회
        Chatroom chatroom = chatroomUtil.getChatroomById(chatroomId, userUuid);

        // 채팅방 참여 인원 수
        long participantSize = chatroom.getChatroomUserList().stream().filter(member -> member.getQuitAt() == null).count();
        if(participantSize < 1)
            chatroomRepository.delete(chatroom);
        else {

            //이미 퇴장했는지 확인
            if(chatroomUser.getQuitAt() != null) {

                CustomLogger.warn(LogEventCode.CHAT,
                        "FAIL",
                        LogEventReasonCode.QUIT_CHATROOM_USER,
                        LogEventReasonCode.QUIT_CHATROOM_USER.getMessage()
                                + " - chatroomId: " + chatroomId
                                + " - chatroomUserId: " + chatroomUser.getId(),
                        userUuid.toString()
                );

                throw new CustomException(CustomExceptionCode.QUIT_CHATROOM_USER);
            }

            // quitAt 기록
            chatroomUser.quitChatroom();

            // 방장이면 방장 계승
            if(chatroom.getHost().getUuid().equals(userUuid)) {

                ChatroomUser newHost = chatroomUserUtil.getOldestChatroomUser(chatroomId, userUuid);
                chatroom.changeHost(newHost.getUser());
            }
        }

        // 퇴장하는 본인이 마지막 멤버가 아님: quitAt만 기록
        // 퇴장하는 본인이 마지막 멤버: 퇴장 후 해당 채팅방 및 채팅 내역도 함께 삭제
        // 방장 채팅방 퇴장 => 입장 이력이 가장 과거인 사람으로 HOST 갱신
    }

    private Slice<Chat> findChat(Long chatroomId, UUID userUuid, Long cursor) {

        int size = 30;
        Pageable pageable = PageRequest.of(0, size);
        Slice<Chat> chatSlice;

        chatSlice = chatRepository.findSlice(chatroomId, userUuid, pageable, cursor);

        return chatSlice;
    }

    private Slice<Chatroom> findChatroom(UUID  userUuid, String keyword, Long cursor) {

        int size = 20;
        Pageable pageable = PageRequest.of(0, size);
        Slice<Chatroom> chatroomSlice;

        chatroomSlice = chatroomRepository.findSlice(userUuid, keyword, pageable, cursor);

        return chatroomSlice;
    }
}
