/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {
    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_SECONDS = 60;

    private final Map<String, RequestWindow> requestWindows = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String clientKey = getClientKey(request);
        RequestWindow window = requestWindows.computeIfAbsent(clientKey, k -> new RequestWindow());

        long now = Instant.now().getEpochSecond();

        // Сброс окна если прошло время
        if (now - window.startTime > WINDOW_SECONDS) {
            window.reset(now);
        }

        window.requestCount++;

        // Проверка лимита
        if (window.requestCount > MAX_REQUESTS) {
            log.warn("Rate limit exceeded for client: {}", clientKey);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader(
                    "X-RateLimit-Reset", String.valueOf(window.startTime + WINDOW_SECONDS));
            response.getWriter().write("Rate limit exceeded. Try again later.");

            return false; // Прервать обработку
        }

        // Добавляем заголовки
        response.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS));
        response.setHeader(
                "X-RateLimit-Remaining", String.valueOf(MAX_REQUESTS - window.requestCount));
        response.setHeader("X-RateLimit-Reset", String.valueOf(window.startTime + WINDOW_SECONDS));

        return true;
    }

    private String getClientKey(HttpServletRequest request) {
        // В реальности: user ID из security context или IP
        return request.getRemoteAddr();
    }

    private static class RequestWindow {
        long startTime;
        int requestCount;

        RequestWindow() {
            this.startTime = Instant.now().getEpochSecond();
            this.requestCount = 0;
        }

        void reset(long now) {
            this.startTime = now;
            this.requestCount = 0;
        }
    }
}
