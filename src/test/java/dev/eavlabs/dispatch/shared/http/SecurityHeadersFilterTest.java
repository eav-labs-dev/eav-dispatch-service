package dev.eavlabs.dispatch.shared.http;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityHeadersFilterTest {

    @Test
    void addsDefensiveHeadersToEveryResponse() throws Exception {
        var response = new MockHttpServletResponse();

        new SecurityHeadersFilter().doFilter(
                new MockHttpServletRequest("GET", "/api/v1/health"),
                response,
                new MockFilterChain()
        );

        assertThat(response.getHeader("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(response.getHeader("X-Frame-Options")).isEqualTo("DENY");
        assertThat(response.getHeader("Referrer-Policy")).isEqualTo("no-referrer");
        assertThat(response.getHeader("Permissions-Policy"))
                .isEqualTo("camera=(), microphone=(), geolocation=()");
    }
}
