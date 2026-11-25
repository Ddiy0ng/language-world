package com.languageworld.be.domain.game.service;

import com.languageworld.be.domain.game.dto.request.GameChatRequestDto;
import com.languageworld.be.domain.game.dto.response.GameChatResponseDto;
import com.languageworld.be.domain.game.entity.GameState;
import com.languageworld.be.domain.game.entity.Participant;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebSocketGameService {

    private final GameroomUtil gameroomUtil;

    public GameChatResponseDto sendGameChat(CustomPrincipal customPrincipal, Long gameroomId, GameChatRequestDto gameChatRequestDto) {

        UUID userUuid = customPrincipal.uuid();

        // 메모리에 방이 있고 참가자인지
        GameState gameState = gameroomUtil.validGameroomMemberCheck(gameroomId, userUuid);
        Participant sender = gameState.getParticipantMap().get(userUuid);

        return GameChatResponseDto.of(sender, gameChatRequestDto.chatMessage());
    }
}
