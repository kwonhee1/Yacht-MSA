package HooYah.User.conf;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogConfig {
    @Around("@within(org.springframework.stereotype.Controller)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime start = LocalDateTime.now();
        log.info("[시작] {}.{} at {}", joinPoint.getSignature().getDeclaringType().getSimpleName(), joinPoint.getSignature().getName(), start);

        try {
            Object result = joinPoint.proceed();
            log.info("[성공] 걸린 시간: {}ms", Duration.between(start, LocalDateTime.now()).toMillis());
            return result;
        } catch (Throwable throwable) {
            log.info("[예외] 걸린 시간: {}ms error {}", Duration.between(start, LocalDateTime.now()).toMillis(), throwable.getMessage());
            throw throwable;
        }
    }

}