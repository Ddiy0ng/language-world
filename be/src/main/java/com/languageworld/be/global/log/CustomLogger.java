package com.languageworld.be.global.log;

import com.languageworld.be.global.enumGroup.LogEventCode;
import com.languageworld.be.global.enumGroup.LogEventReasonCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomLogger {

    public static void info(LogEventCode logEventCode, String result, String description, Long userId) {

        log.info(
                "event = {}  result = {} description = {} userId = {}",
                logEventCode,
                result,
                description,
                userId
        );
    }

    public static void warn(LogEventCode logEventCode, String result, LogEventReasonCode logEventReasonCode, String description, Long userId) {

        log.warn(
                "event = {}  result = {}  reason = {} data = {} userId = {}",
                logEventCode,
                result,
                logEventReasonCode,
                description,
                userId
        );
    }
}
