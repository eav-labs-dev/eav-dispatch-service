package dev.eavlabs.dispatch.shared.http;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

/** Verifies the public throttling and request-size contracts. */
class HttpProtectionFilterTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-10-03T08:00:10Z"),
            ZoneOffset.UTC
    );

    @Test
    void returnsStable429AfterClientExceedsLimit() throws Exception {
        var filter = filterWith(2, 1_024);

        assertThat(execute(filter, "/api/v1/shipments", new byte[0]).getStatus()).isEqualTo(200);
        assertThat(execute(filter, "/api/v1/shipments", new byte[0]).getStatus()).isEqualTo(200);

        var response = execute(filter, "/api/v1/shipments", new byte[0]);

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getHeader("Retry-After")).isEqualTo("50");
        assertThat(response.getHeader("X-RateLimit-Limit")).isEqualTo("2");
        assertThat(response.getContentAsString()).contains("\"code\":\"RATE_LIMIT_EXCEEDED\"");
    }

    @Test
    void exemptsPublicHealthEndpoint() throws Exception {
        var filter = filterWith(1, 1_024);

        assertThat(execute(filter, "/api/v1/health", new byte[0]).getStatus()).isEqualTo(200);
        assertThat(execute(filter, "/api/v1/health", new byte[0]).getStatus()).isEqualTo(200);
    }

    @Test
    void rejectsDeclaredBodiesAboveConfiguredLimit() throws Exception {
        var response = execute(filterWith(10, 4), "/api/v1/shipments", new byte[5]);

        assertThat(response.getStatus()).isEqualTo(413);
        assertThat(response.getContentAsString()).contains("\"code\":\"REQUEST_TOO_LARGE\"");
    }

    private HttpProtectionFilter filterWith(int limit, long maxBodyBytes) {
        return new HttpProtectionFilter(
                new HttpProtectionProperties(limit, maxBodyBytes),
                FIXED_CLOCK
        );
    }

    private MockHttpServletResponse execute(HttpProtectionFilter filter, String path, byte[] content)
            throws Exception {
        var request = new MockHttpServletRequest("POST", path);
        request.setRemoteAddr("203.0.113.10");
        request.setContent(content);
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }
}
