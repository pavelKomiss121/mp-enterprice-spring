/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.aspect;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.mentee.banking.annotation.Cacheable;

@Slf4j
@Aspect
@Component
public class CachingAspect {

    private final ConcurrentMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    @Around("@annotation(cacheable)")
    public Object cache(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
        String cacheKey = generateCacheKey(joinPoint, cacheable.cacheName());
        CacheEntry entry = cache.get(cacheKey);

        if (entry != null && !entry.isExpired(cacheable.ttl())) {
            log.debug("Cache hit for key: {}", cacheKey);
            return entry.getValue();
        }

        log.debug("Cache miss for key: {}", cacheKey);
        Object result = joinPoint.proceed();

        cache.put(cacheKey, new CacheEntry(result, System.currentTimeMillis()));

        return result;
    }

    private String generateCacheKey(ProceedingJoinPoint joinPoint, String cacheName) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        return cacheName + ":" + methodName + ":" + Arrays.hashCode(args);
    }

    private static class CacheEntry {
        private final Object value;
        private final long timestamp;

        public CacheEntry(Object value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        public Object getValue() {
            return value;
        }

        public boolean isExpired(long ttlSeconds) {
            return (System.currentTimeMillis() - timestamp) > (ttlSeconds * 1000);
        }
    }
}
