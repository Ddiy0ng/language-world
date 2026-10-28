package com.languageworld.be.domain.user.service;

import com.languageworld.be.domain.user.dto.response.FriendRequestResponseDto;
import com.languageworld.be.domain.user.entity.Friend;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.enumGroup.FriendStatusCode;
import com.languageworld.be.domain.user.repository.FriendRepository;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserUtil userUtil;
    private final FriendUtil friendUtil;
    private final BlockedUserUtil blockedUserUtil;

    @Transactional
    public void addFriend(CustomPrincipal customPrincipal, UUID friendUuid) {

        UUID userUuid = customPrincipal.uuid();
        User authenticatedUser = userUtil.getUserByUuid(userUuid);

        // 스스로 추가 방지
        if(userUuid.equals(friendUuid)) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.CANT_ADD_SELF_AS_FRIEND,
                    LogEventReasonCode.CANT_ADD_SELF_AS_FRIEND.getMessage()
                            + " - authenticatedUser uuid: " + userUuid
                            + " - friend uuid: " + friendUuid,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.CANT_ADD_SELF_AS_FRIEND);
        }

        // 사용자 존재 확인
        User requestedUser = userUtil.getUserByUuid(friendUuid);

        // 인증된 사용자와 친구 여부 확인
        if(friendRepository.existsByUser_UuidAndFriend_Uuid(userUuid, friendUuid)) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.ALREADY_FRIEND,
                    LogEventReasonCode.ALREADY_FRIEND.getMessage()
                            + " - authenticatedUser uuid: " + userUuid
                            + " - friend uuid: " + friendUuid,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.ALREADY_FRIEND);
        }

        // TODO 차단 여부 확인
        blockedUserUtil.blockedUserCheck(userUuid, friendUuid);

        // 추가
        Friend friend = Friend.of(authenticatedUser, requestedUser);

        friendRepository.save(friend);
    }

    @Transactional
    public List<FriendRequestResponseDto> findSentFriendRequestList(CustomPrincipal customPrincipal) {

        UUID uuid = customPrincipal.uuid();
        List<FriendStatusCode> friendStatusCodeList= List.of(FriendStatusCode.REQUESTED, FriendStatusCode.REJECTED);
        List<Friend> sentFriendRequestList = friendRepository.findAllSentFriendRequest(uuid, friendStatusCodeList);

        List<FriendRequestResponseDto> friendRequestResponseDtoList = sentFriendRequestList.stream()
                .map(FriendRequestResponseDto::fromSent)
                .toList();

        return friendRequestResponseDtoList;
    }

    @Transactional
    public List<FriendRequestResponseDto> findReceivedFriendRequestList(CustomPrincipal customPrincipal) {

        UUID uuid = customPrincipal.uuid();
        List<Friend> sentFriendRequestList = friendRepository.findAllReceivedFriendRequest(uuid, FriendStatusCode.REQUESTED);

        List<FriendRequestResponseDto> friendRequestResponseDtoList = sentFriendRequestList.stream()
                .map(FriendRequestResponseDto::fromReceived)
                .toList();

        return friendRequestResponseDtoList;
    }

    @Transactional
    public List<FriendRequestResponseDto> searchFriendList(CustomPrincipal customPrincipal, String keyword) {

        // TODO 차단된 유저는 조회할 수 없도록

        // Friend 테이블의 customPrincipal id,  friendStatus: ACCEPTED 기준 검색
        UUID uuid = customPrincipal.uuid();
        List<Friend> friendList = friendRepository.findAllFriendUsers(customPrincipal.uuid(), keyword, FriendStatusCode.ACCEPTED);

        List<FriendRequestResponseDto> friendRequestResponseDtoList = friendList.stream()
                .map(friend -> FriendRequestResponseDto.from(uuid, friend))
                .toList();

        return friendRequestResponseDtoList;
    }

    @Transactional
    public void acceptFriendRequest(CustomPrincipal customPrincipal, Long requestId) {

        UUID uuid = customPrincipal.uuid();

        // 요청받은 건인지 확인
        Friend friend = friendUtil.getReceivedFriendRequest(uuid, requestId);

        // 요청 상태인지 확인
        friendUtil.validateFriendStatus(friend, FriendStatusCode.REQUESTED, uuid);

        friend.acceptFriendRequest();
    }

    @Transactional
    public void rejectFriendRequest(CustomPrincipal customPrincipal, Long requestId) {

        UUID uuid = customPrincipal.uuid();

        // 요청받은 건인지 확인
        Friend friend = friendUtil.getReceivedFriendRequest(uuid, requestId);

        // 요청 상태인지 확인
        friendUtil.validateFriendStatus(friend, FriendStatusCode.REQUESTED, uuid);

        friend.rejectFriendRequest();
    }

    @Transactional
    public void cancelFriendRequest(CustomPrincipal customPrincipal, Long requestId) {

        UUID uuid = customPrincipal.uuid();
        Friend friend = friendUtil.getSentFriendRequest(uuid, requestId);

        // 요청 | 거절  상태인지 확인()
        friendUtil.validateFriendStatusForCancel(friend, uuid);

        friendRepository.delete(friend);
    }

    @Transactional
    public void removeFriend(CustomPrincipal customPrincipal, Long friendId) {

        // 친구 삭제 => 수락 상태인 것만 삭제
        UUID uuid = customPrincipal.uuid();

        Friend friend = friendUtil.getAcceptedFriendRequest(uuid, friendId);
        if(!friend.getUser().getUuid().equals(uuid) && !friend.getFriend().getUuid().equals(uuid)) {

            CustomLogger.warn(
                    LogEventCode.USER,
                    "FAIL",
                    LogEventReasonCode.FORBIDDEN_FRIEND_REMOVE_REQUEST,
                    LogEventReasonCode.FORBIDDEN_FRIEND_REMOVE_REQUEST.getMessage()
                            + " - authenticatedUser uuid: " + uuid
                            + " - requestId: " + friendId,
                    uuid.toString()
            );

            throw new CustomException(CustomExceptionCode.FORBIDDEN_FRIEND_REMOVE_REQUEST);
        }

        friendRepository.delete(friend);
    }
}
