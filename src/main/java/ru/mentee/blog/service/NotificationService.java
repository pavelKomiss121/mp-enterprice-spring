/* @MENTEE_POWER (C)2026 */
package ru.mentee.blog.service;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.mentee.blog.domain.model.Post;

@Slf4j
@Service
public class NotificationService {

    @Async
    public CompletableFuture<Void> sendPostNotification(Post post) {
        log.info("📧 Начинаем отправку email о посте: {}", post.getTitle());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("✅ Email отправлен для поста: {}", post.getTitle());
        return CompletableFuture.completedFuture(null);
    }
}
