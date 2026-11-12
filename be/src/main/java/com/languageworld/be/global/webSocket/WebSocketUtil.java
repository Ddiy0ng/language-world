package com.languageworld.be.global.webSocket;

import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class WebSocketUtil {

    private static final Pattern SUB_PATTERN = Pattern.compile("^/sub/chatroomId/([1-9][0-9]*)$");
    private static final Pattern PUB_PATTERN = Pattern.compile("^/pub/chatroomId/([1-9][0-9]*)$");

    //roomId 추출
    public Long extractChatroomId(StompHeaderAccessor accessor, StompCommand stompCommand){

        String destination = accessor.getDestination();
        if(destination == null || destination.isBlank()) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.STOMP_HEADER_ACCESSOR_DESTINATION_IS_NULL_OR_BLANK,
                    LogEventReasonCode.STOMP_HEADER_ACCESSOR_DESTINATION_IS_NULL_OR_BLANK.getMessage(),
                    null
            );
            throw new CustomException(CustomExceptionCode.WEBSOCKET_BAD_REQUEST);
        }

        switch (stompCommand) {

            case SUBSCRIBE -> {

                return getChatroomIdIfDestAndPatternMatch(destination, SUB_PATTERN);
            }
            case SEND -> {

                return getChatroomIdIfDestAndPatternMatch(destination, PUB_PATTERN);
            }
            default -> {

                CustomLogger.warn(
                        LogEventCode.WEBSOCKET,
                        "FAIL",
                        LogEventReasonCode.STOMP_COMMAND_NOT_REQUIRED_EXTRACT_ROOMID_METHOD,
                        LogEventReasonCode.STOMP_COMMAND_NOT_REQUIRED_EXTRACT_ROOMID_METHOD.getMessage(),
                        null
                );

                throw new CustomException(CustomExceptionCode.WEBSOCKET_BAD_REQUEST);
            }
        }
    }

    private Long getChatroomIdIfDestAndPatternMatch(String destination, Pattern pattern) {

        Matcher matcher = pattern.matcher(destination);

        if(!matcher.matches()) {

            CustomLogger.warn(
                    LogEventCode.WEBSOCKET,
                    "FAIL",
                    LogEventReasonCode.DESTINATION_NOT_MATCH_WITH_PATTERN,
                    LogEventReasonCode.DESTINATION_NOT_MATCH_WITH_PATTERN.getMessage()
                            + " - destination: " + destination
                            + " - Pattern: " + pattern,
                    null
            );

            throw new CustomException(CustomExceptionCode.WEBSOCKET_BAD_REQUEST);
        }

        return Long.valueOf(matcher.group(1));
    }

}
