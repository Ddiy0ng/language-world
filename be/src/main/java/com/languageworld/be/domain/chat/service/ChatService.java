package com.languageworld.be.domain.chat.service;

import com.languageworld.be.domain.chat.dto.request.ChatSaveRequestDto;
import com.languageworld.be.domain.chat.dto.response.ChatResponseDto;
import com.languageworld.be.domain.chat.dto.response.FriendChatroomEnterResponseDto;
import com.languageworld.be.domain.chat.dto.response.ChatroomResponseDto;
import com.languageworld.be.domain.chat.entity.Chatroom;
import com.languageworld.be.domain.chat.entity.ChatroomUser;
import com.languageworld.be.domain.chat.enumGroup.ChatroomTypeCode;
import com.languageworld.be.domain.chat.repository.ChatRepository;
import com.languageworld.be.domain.chat.repository.ChatroomRepository;
import com.languageworld.be.domain.chat.repository.ChatroomUserRepository;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.service.UserUtil;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    public void saveChat(CustomPrincipal customPrincipal, ChatSaveRequestDto chatSaveRequestDto) {


    }

    @Transactional
    public List<ChatResponseDto> findChatList(CustomPrincipal customPrincipal, Long chatroomId) {
    }

    @Transactional
    public List<ChatroomResponseDto> findChatroomList(CustomPrincipal customPrincipal) {
    }

    @Transactional
    public void quitChatroom(CustomPrincipal customPrincipal, Long chatRoomId) {

        if()
        // 퇴장하는 본인이 마지막 멤버가 아님: quitAt만 기록
        // 퇴장하는 본인이 마지막 멤버: 퇴장 후 해당 채팅방 및 채팅 내역도 함께 삭제
        // 채팅방 퇴장 => 입장 이력이 가장 최근인 사람으로 HOST 갱신
    }
}
