package com.languageworld.be.global.log;

import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class CustomLogger {

    public static void info(LogEventCode logEventCode, String result, String description, String userUuid) {

        log.info(
                "event = {}  result = {} description = {} userUuid = {}",
                logEventCode,
                result,
                description,
                userUuid
        );
    }

    public static void warn(LogEventCode logEventCode, String result, LogEventReasonCode logEventReasonCode, String description, String userUuid) {

        log.warn(
                "event = {}  result = {}  reason = {} description = {} userUuid = {}",
                logEventCode,
                result,
                logEventReasonCode,
                description,
                userUuid
        );
    }

    public static void error(LogEventCode logEventCode, String result, LogEventReasonCode logEventReasonCode, String description, Exception e) {

        log.error(
                "event = {}  result = {}  reason = {} description = {}",
                logEventCode,
                result,
                logEventReasonCode,
                description,
                e
        );
    }
}
