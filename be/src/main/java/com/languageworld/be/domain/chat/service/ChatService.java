package com.languageworld.be.domain.chat.service;

import com.languageworld.be.domain.chat.dto.response.*;
import com.languageworld.be.domain.chat.entity.Chat;
import com.languageworld.be.domain.chat.entity.Chatroom;
import com.languageworld.be.domain.chat.entity.ChatroomUser;
import com.languageworld.be.domain.chat.enumGroup.ChatroomTypeCode;
import com.languageworld.be.domain.chat.repository.ChatRepository;
import com.languageworld.be.domain.chat.repository.ChatroomRepository;
import com.languageworld.be.domain.chat.repository.ChatroomUserRepository;
import com.languageworld.be.domain.user.dto.response.UserSummaryResponseDto;
import com.languageworld.be.domain.user.entity.User;
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

    // 친구창 통해 채팅방 입장(dm)
    @Transactional
    public FriendChatroomEnterResponseDto enterFriendChatroom(CustomPrincipal customPrincipal, UUID userUuid) {

        UUID authenticatedUserUuid = customPrincipal.uuid();

        // personal 채팅으로 내 uuid 또는 친구 uuid가 host이면서 둘의 uuid의 user를 참조하는 chatroomUser레코드가 존재하는 채팅방 있는지 확인
        // => 채팅방, 채팅유저 이력은 확인(채팅방 현재 참여 여부는 미확인)
        // 존재: 나의 채팅방 현재 참여 여부 확인, 미참여 시 참여로 바꿈(quitAt을 null로)
        // 미존재: 채팅방, 채팅 유저 생성해 저장
        // 최종: chatroomId 반환
        Optional<Chatroom> optionalChatroom = chatroomRepository.findByChatroomTypeCodeAndHostAndChatroomUser(ChatroomTypeCode.PERSONAL, authenticatedUserUuid, userUuid);

        if(optionalChatroom.isPresent()) {

            Chatroom chatroom = optionalChatroom.get();
            // 본인 이전 퇴장 여부 확인, 재입장(quitAt을 null로)
            ChatroomUser chatroomUser = chatroomUserUtil.getChatroomUserByChatroomIdAndUserUuid(chatroom.getId(), authenticatedUserUuid);
            if(chatroomUser.getQuitAt() != null)
                chatroomUser.reEnterChatroom();

            return FriendChatroomEnterResponseDto.from(optionalChatroom.get().getId());
        }
        else {

            User authenticatedUser = userUtil.getUserByUuid(authenticatedUserUuid);
            User friend = userUtil.getUserByUuid(userUuid);

            // dm은 채팅방 제목 미지정
            Chatroom chatroom = Chatroom.of(ChatroomTypeCode.PERSONAL, 2, null, authenticatedUser);
            chatroomRepository.save(chatroom);

            ChatroomUser chatroomUserAuth = ChatroomUser.of(authenticatedUser, chatroom);
            chatroomUserRepository.save(chatroomUserAuth);
            ChatroomUser chatroomUserFriend = ChatroomUser.of(friend, chatroom);
            chatroomUserRepository.save(chatroomUserFriend);

            return FriendChatroomEnterResponseDto.from(chatroom.getId());
        }
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

        if(chatroom.getChatroomUserList().stream().filter(member -> member.getQuitAt()== null).count() <= 1)
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
        // 방장 채팅방 퇴장 => 입장 이력이 가장 최근인 사람으로 HOST 갱신
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
