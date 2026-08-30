package ar.com.lbr.precisionappbe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/auth/login";
    private static final int MAX_ATTEMPTS = 10;
    private static final long WINDOW_SECONDS = 900L; // 15 minutos

    private final ConcurrentHashMap<String, AttemptWindow> attempts = new ConcurrentHashMap<>();

    public LoginRateLimitFilter() {
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!"POST".equals(request.getMethod()) || !LOGIN_PATH.equals(request.getServletPath())) {
            return true;
        }
        String ip = resolveClientIp(request);
        return "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip) || "localhost".equals(ip);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String ip = resolveClientIp(request);
        long now = Instant.now().getEpochSecond();
        cleanupExpiredEntries(now);

        AttemptWindow window = attempts.compute(ip, (key, existing) -> {
            if (existing == null || now - existing.windowStart >= WINDOW_SECONDS) {
                return new AttemptWindow(now, new AtomicInteger(1));
            }
            existing.count.incrementAndGet();
            return existing;
        });

        if (window.count.get() > MAX_ATTEMPTS) {
            long retryAfter = WINDOW_SECONDS - (now - window.windowStart);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", String.valueOf(retryAfter));
            response.getWriter().write(
                    "{\"success\":false,\"message\":\"Demasiados intentos de login. Intente en "
                            + retryAfter + " segundos.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void cleanupExpiredEntries(long now) {
        if (attempts.size() > 50) {
            attempts.entrySet().removeIf(entry -> now - entry.getValue().windowStart >= WINDOW_SECONDS);
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        // Only trust X-Forwarded-For when the direct connection comes from localhost (reverse proxy on same host)
        if ("127.0.0.1".equals(remoteAddr) || "0:0:0:0:0:0:0:1".equals(remoteAddr)) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
        }
        return remoteAddr;
    }

    private static class AttemptWindow {
        final long windowStart;
        final AtomicInteger count;

        AttemptWindow(long windowStart, AtomicInteger count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
