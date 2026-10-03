package dev.eavlabs.dispatch.shared.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eavlabs.dispatch.shared.api.ApiError;
import dev.eavlabs.dispatch.shared.api.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Applies lightweight per-client throttling and declared body-size limits.
 *
 * <p>The deployed MVP runs one application instance. A shared store is required before
 * horizontally scaling this limiter.</p>
 */
@Component
public class HttpProtectionFilter extends OncePerRequestFilter {

    private static final long WINDOW_SECONDS = 60;
    private static final int CLEANUP_THRESHOLD = 10_000;

    private final HttpProtectionProperties properties;
    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public HttpProtectionFilter(HttpProtectionProperties properties, ObjectMapper objectMapper) {
        this(properties, objectMapper, Clock.systemUTC());
    }

    HttpProtectionFilter(HttpProtectionProperties properties, ObjectMapper objectMapper, Clock clock) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/") || path.equals("/api/v1/health");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long contentLength = request.getContentLengthLong();
        if (contentLength > properties.maxRequestBodyBytes()) {
            writeFailure(
                    response,
                    HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
                    "REQUEST_TOO_LARGE",
                    "Request body exceeds the configured limit"
            );
            return;
        }

        long currentWindow = clock.instant().getEpochSecond() / WINDOW_SECONDS;
        Window window = windows.compute(request.getRemoteAddr(), (key, existing) -> {
            if (existing == null || existing.minute() != currentWindow) {
                return new Window(currentWindow, 1);
            }
            return new Window(currentWindow, existing.count() + 1);
        });

        int remaining = Math.max(0, properties.requestsPerMinute() - window.count());
        response.setHeader("X-RateLimit-Limit", String.valueOf(properties.requestsPerMinute()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));

        if (window.count() > properties.requestsPerMinute()) {
            long retryAfter = WINDOW_SECONDS - (clock.instant().getEpochSecond() % WINDOW_SECONDS);
            response.setHeader("Retry-After", String.valueOf(retryAfter));
            writeFailure(
                    response,
                    HttpServletResponse.SC_TOO_MANY_REQUESTS,
                    "RATE_LIMIT_EXCEEDED",
                    "Too many requests. Retry after the current rate-limit window."
            );
            return;
        }

        if (windows.size() > CLEANUP_THRESHOLD) {
            windows.entrySet().removeIf(entry -> entry.getValue().minute() < currentWindow);
        }
        filterChain.doFilter(request, response);
    }

    private void writeFailure(HttpServletResponse response, int status, String code, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        var error = new ApiError(code, Map.of());
        objectMapper.writeValue(response.getOutputStream(), ApiResponse.failure(code, message, error));
    }

    private record Window(long minute, int count) {
    }
}
