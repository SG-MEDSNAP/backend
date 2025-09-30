package backend.medsnap.domain.notification.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Token Bucket 알고리즘을 사용한 Rate Limiter
 * Expo Push Service의 600개/초 제한을 준수
 */
@Slf4j
@Component
public class RateLimiter {

    private static final int MAX_TOKENS = 600; // 초당 최대 토큰 수
    private static final long REFILL_INTERVAL_MS = 1000; // 1초마다 리필

    private final AtomicInteger tokens = new AtomicInteger(MAX_TOKENS);
    private final AtomicLong lastRefillTime = new AtomicLong(System.currentTimeMillis());

    /**
     * 토큰을 획득하려고 시도
     */
    public boolean tryAcquire(int requestedTokens) {
        if (requestedTokens <= 0) {
            return true;
        }

        refillTokens();
        
        while (true) {
            int currentTokens = tokens.get();
            if (currentTokens < requestedTokens) {
                log.debug("토큰 부족: 요청={}, 현재 토큰={}", requestedTokens, currentTokens);
                return false;
            }
            
            int newTokens = currentTokens - requestedTokens;
            if (tokens.compareAndSet(currentTokens, newTokens)) {
                log.debug("토큰 획득 성공: 요청={}, 남은 토큰={}", requestedTokens, newTokens);
                return true;
            }
            // CAS 실패 시 재시도
        }
    }

    /**
     * 토큰을 획득할 때까지 대기
     */
    public void acquire(int requestedTokens) {
        while (!tryAcquire(requestedTokens)) {
            try {
                Thread.sleep(10); // 10ms 대기 후 재시도
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Rate limiter interrupted", e);
            }
        }
    }

    /**
     * 현재 사용 가능한 토큰 수
     */
    public int getAvailableTokens() {
        refillTokens();
        return tokens.get();
    }

    /**
     * 토큰 리필 로직
     */
    private void refillTokens() {
        long now = System.currentTimeMillis();
        long lastRefill = lastRefillTime.get();

        if (now - lastRefill >= REFILL_INTERVAL_MS) {
            if (lastRefillTime.compareAndSet(lastRefill, now)) {
                // 1초마다 MAX_TOKENS로 리셋
                tokens.set(MAX_TOKENS);
                log.debug("토큰 리필: MAX_TOKENS={}", MAX_TOKENS);
            }
        }
    }
}
