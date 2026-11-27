package com.languageworld.be.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class GameSchedulerConfig {

    private static final int GAME_SCHEDULER_THREAD_SIZE = 2;

    // 게임 예약 작업(카운트다운 뒤 1라운드, 라운드 제한 시간, 라운드 사이 쉬는 시간, 다음 매치 대기)
    @Bean(destroyMethod = "shutdownNow")
    public ScheduledExecutorService gameScheduler() {

        AtomicInteger threadNumber = new AtomicInteger(1);

        return Executors.newScheduledThreadPool(GAME_SCHEDULER_THREAD_SIZE, runnable -> {
            Thread thread = new Thread(runnable, "game-thread-" + threadNumber.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        });
    }
}