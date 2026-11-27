package com.languageworld.be.domain.game.service;

import com.languageworld.be.domain.game.repository.GameStateManage;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.response.enumGroup.SuccessCode;
import com.languageworld.be.global.webSocket.service.WebSocketSessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.UUID;

// STOMP 연결이 끊기면(탭 닫기·새로고침·네트워크 끊김 포함) 참여 중인 게임방에서 자동 퇴장
@Component
@RequiredArgsConstructor
public class WebSocketGameDisconnectListener {

    private final WebSocketSessionUtil webSocketSessionUtil;
    private final GameStateManage gameStateManage;
    private final GameroomService gameroomService;

    @EventListener
    public void leaveGameroomOnDisconnect(SessionDisconnectEvent sessionDisconnectEvent) {

        // DISCONNECT 프레임 없이 끊긴 경우(탭 닫기)에도 세션 정리
        String sessionId = sessionDisconnectEvent.getSessionId();
        if(sessionId != null && !sessionId.isBlank())
            webSocketSessionUtil.removeWebSocketSession(sessionId);

        // CONNECT에서 인증된 사용자만
        Principal principal = sessionDisconnectEvent.getUser();
        if(!(principal instanceof Authentication authentication) || !(authentication.getPrincipal() instanceof CustomPrincipal customPrincipal))
            return;

        UUID userUuid = customPrincipal.uuid();

        // 같은 사용자의 다른 연결(다른 탭)이 남아 있으면 나가지 않음
        if(webSocketSessionUtil.hasWebSocketSessionByUserUuid(userUuid))
            return;

        // 끊김 이벤트는 한 연결에 두 번 올 수 있으므로, 아직 방에 있을 때만 퇴장
        gameStateManage.findGameroomIdByUserUuid(userUuid).ifPresent(gameroomId -> {

            gameroomService.leaveGameroom(userUuid, gameroomId);

            CustomLogger.info(
                    LogEventCode.GAME,
                    "SUCCESS",
                    SuccessCode.GAMEROOM_QUIT_SUCCESS.getMessage() + " - (disconnected) gameroomId: " + gameroomId,
                    userUuid.toString()
            );
        });
    }
}
