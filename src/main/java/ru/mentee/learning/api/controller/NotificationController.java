/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.api.controller;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@Slf4j
public class NotificationController {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications() {
        log.info("GET /api/v1/notifications/stream - starting SSE stream");

        SseEmitter emitter = new SseEmitter(30_000L); // 30 секунд

        // Отправляем периодические уведомления
        executor.scheduleAtFixedRate(
                () -> {
                    try {
                        String message = "Notification at " + System.currentTimeMillis();
                        emitter.send(
                                SseEmitter.event()
                                        .name("notification")
                                        .id(UUID.randomUUID().toString())
                                        .data(message));
                        log.debug("Sent SSE event: {}", message);
                    } catch (IOException e) {
                        log.warn("SSE stream error: {}", e.getMessage());
                        emitter.completeWithError(e);
                    }
                },
                0,
                3,
                TimeUnit.SECONDS);

        emitter.onCompletion(() -> log.info("SSE stream completed"));
        emitter.onTimeout(
                () -> {
                    log.info("SSE stream timeout");
                    emitter.complete();
                });
        emitter.onError(e -> log.error("SSE stream error", e));

        return emitter;
    }
}
