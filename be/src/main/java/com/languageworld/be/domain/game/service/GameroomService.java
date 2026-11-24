package com.languageworld.be.domain.game.service;

import com.languageworld.be.domain.game.dto.request.GameroomCreateRequestDto;
import com.languageworld.be.domain.game.dto.request.GameroomEnterRequestDto;
import com.languageworld.be.domain.game.dto.response.GameroomCreateResponseDto;
import com.languageworld.be.domain.game.dto.response.GameroomResponseDto;
import com.languageworld.be.domain.game.entity.GameState;
import com.languageworld.be.domain.game.entity.Gameroom;
import com.languageworld.be.domain.game.enumGroup.GameroomStatusCode;
import com.languageworld.be.domain.game.repository.GameroomRepository;
import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.language.service.LanguageUtil;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.service.UserUtil;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.auth.service.PasswordUtil;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
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
public class GameroomService {

    private final GameroomRepository gameroomRepository;
    private final GameroomUtil gameroomUtil;
    private final GameStateUtil gameStateUtil;
    private final UserUtil userUtil;
    private final LanguageUtil languageUtil;
    private final PasswordUtil passwordUtil;

    // 생성: DB 저장 → 메모리에 GameState 등록 → 방장 자동 입장
    @Transactional
    public GameroomCreateResponseDto createGameroom(CustomPrincipal customPrincipal, GameroomCreateRequestDto dto) {

        // 유효한 사용자 검증
        UUID hostUuid = customPrincipal.uuid();
        userUtil.deletedUserCheckByUserUuid(hostUuid);
        User host = userUtil.getUserByUuid(hostUuid);

        // 한 사람 한 방
        if(gameStateUtil.findGameroomIdByUserUuid(hostUuid).isPresent()) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.ALREADY_IN_GAMEROOM,
                    LogEventReasonCode.ALREADY_IN_GAMEROOM.getMessage(),
                    hostUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.ALREADY_IN_GAMEROOM);
        }

        // 학습 언어와 레벨이 서로 맞는지
        Language language = languageUtil.getLanguageById(dto.learningLanguageId());
        Level level = languageUtil.getLevelById(dto.levelId());
        if(!level.getLanguage().getId().equals(language.getId())) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.GAMEROOM_LEVEL_LANGUAGE_MISMATCH,
                    LogEventReasonCode.GAMEROOM_LEVEL_LANGUAGE_MISMATCH.getMessage()
                            + " - languageId: " + language.getId() + ", levelId: " + level.getId(),
                    hostUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.GAMEROOM_LEVEL_LANGUAGE_MISMATCH);
        }

        // 잠긴 방이면 비밀번호 필수 + BCrypt 저장, 안 잠긴 방이면 저장하지 않음
        String encodedPassword = null;
        if(dto.isLocked()) {
            if(dto.password() == null || dto.password().isBlank()) {

                CustomLogger.warn(
                        LogEventCode.GAME,
                        "FAIL",
                        LogEventReasonCode.GAMEROOM_PASSWORD_REQUIRED,
                        LogEventReasonCode.GAMEROOM_PASSWORD_REQUIRED.getMessage(),
                        hostUuid.toString()
                );

                throw new CustomException(CustomExceptionCode.GAMEROOM_PASSWORD_REQUIRED);
            }

            encodedPassword = passwordUtil.encodePassword(dto.password());
        }

        // gameroom 생성 및 저장
        Gameroom gameroom = gameroomRepository.save(Gameroom.of(dto, encodedPassword, language, level, host));

        // gameState 생성, host를 참가자로 추가, 메모리에 등록
        GameState gameState = GameState.of(gameroom.getId(), gameroom.getMaxPlayerSize(), gameroom.getRoundSize(), hostUuid);
        gameState.addParticipant(hostUuid, host.getName());
        gameStateUtil.saveGameroomGameState(gameState);

        return new GameroomCreateResponseDto(gameroom.getId());
    }

    // 목록: 메모리에 있고 자리가 남은 방(WAIT, STARTED)
    @Transactional
    public List<GameroomResponseDto> findGameroomList() {

        List<Long> joinableIdList = gameStateUtil.findJoinableGameroomIdList();
        if(joinableIdList.isEmpty())
            return List.of();

        return gameroomRepository.findAllByIdInAndGameroomStatusNotOrderByIdDesc(joinableIdList, GameroomStatusCode.CLOSED).stream()
                .map(gameroom -> GameroomResponseDto.of(
                        gameroom,
                        gameStateUtil.findOptionalGameState(gameroom.getId())
                                .map(GameState::getParticipantSize)
                                .orElse(0)))
                .toList();
    }

    // 입장: 대기 중·진행 중 모두 가능
    @Transactional
    public void enterGameroom(CustomPrincipal customPrincipal, Long gameroomId, GameroomEnterRequestDto dto) {

        UUID userUuid = customPrincipal.uuid();
        userUtil.deletedUserCheckByUserUuid(userUuid);
        User user = userUtil.getUserByUuid(userUuid);

        Gameroom gameroom = gameroomUtil.getGameroomById(gameroomId, userUuid);
        GameState gameState = gameroomUtil.getGameStateById(gameroomId, userUuid);

        // 이미 이 방에 있으면 그대로 성공
        if(gameState.contains(userUuid))
            return;

        // 정원 확인
        if(gameState.isFull()) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.GAMEROOM_FULL,
                    LogEventReasonCode.GAMEROOM_FULL.getMessage()
                            + " - gameroomId: " + gameroomId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.GAMEROOM_FULL);
        }

        // 다른 방에 있으면 입장 불가(한 사람 한 방)
        if(gameStateUtil.findGameroomIdByUserUuid(userUuid).isPresent()) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.ALREADY_IN_GAMEROOM,
                    LogEventReasonCode.ALREADY_IN_GAMEROOM.getMessage()
                            + " - gameroomId: " + gameroomId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.ALREADY_IN_GAMEROOM);
        }

        // 잠긴 방 비밀번호 확인 (잠기지 않은 방은 body 없이 요청 → dto == null)
        if(gameroom.isLocked()) {

            String rawPassword = dto == null ? null : dto.password();
            if(rawPassword == null || rawPassword.isBlank()) {

                CustomLogger.warn(
                        LogEventCode.GAME,
                        "FAIL",
                        LogEventReasonCode.GAMEROOM_PASSWORD_REQUIRED,
                        LogEventReasonCode.GAMEROOM_PASSWORD_REQUIRED.getMessage()
                                + " - gameroomId: " + gameroomId,
                        userUuid.toString()
                );

                throw new CustomException(CustomExceptionCode.GAMEROOM_PASSWORD_REQUIRED);
            }

            if(!passwordUtil.isPasswordMatches(rawPassword, gameroom.getPassword())) {

                CustomLogger.warn(
                        LogEventCode.GAME,
                        "FAIL",
                        LogEventReasonCode.GAMEROOM_PASSWORD_MISMATCH,
                        LogEventReasonCode.GAMEROOM_PASSWORD_MISMATCH.getMessage()
                                + " - gameroomId: " + gameroomId,
                        userUuid.toString()
                );

                throw new CustomException(CustomExceptionCode.GAMEROOM_PASSWORD_MISMATCH);
            }
        }

        gameState.addParticipant(userUuid, user.getName());

        // PARTICIPANT_JOINED 브로드캐스트
    }

    // 퇴장: 방장이면 가장 먼저 들어온 사람에게 위임, 마지막 사람이면 방 닫기
    @Transactional
    public void quitGameroom(CustomPrincipal customPrincipal, Long gameroomId) {

        UUID userUuid = customPrincipal.uuid();
        Gameroom gameroom = gameroomUtil.getGameroomById(gameroomId, userUuid);
        GameState gameState = gameroomUtil.getGameStateById(gameroomId, userUuid);

        if(!gameState.contains(userUuid)) {

            CustomLogger.warn(
                    LogEventCode.GAME,
                    "FAIL",
                    LogEventReasonCode.NOT_GAMEROOM_MEMBER,
                    LogEventReasonCode.NOT_GAMEROOM_MEMBER.getMessage()
                            + " - gameroomId: " + gameroomId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.NOT_GAMEROOM_MEMBER);
        }

        gameState.removeParticipant(userUuid);

        if(gameState.getParticipantSize() == 0) {
            // 메모리에서 제거 + DB 상태 CLOSED (변경 감지로 커밋 시 UPDATE)
            gameStateUtil.remove(gameroomId);
            gameroom.changeStatus(GameroomStatusCode.CLOSED);
        } else if(userUuid.equals(gameState.getHostUuid())) {
            UUID newHostUuid = gameState.getOldestParticipantUuid();
            gameState.changeHost(newHostUuid);
            gameroom.changeHost(userUtil.getUserByUuid(newHostUuid));
        }
    }
}