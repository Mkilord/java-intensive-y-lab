package autoservice.servlet.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Cleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;

class JWTFilterTest {

    @InjectMocks
    private JWTFilter jwtFilter;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpServletResponse httpResponse;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDoFilter_MissingAuthorizationHeader() throws IOException {
        when(httpRequest.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilter(httpRequest, httpResponse, filterChain);

        verify(httpResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
        verifyNoInteractions(filterChain);
    }

    @Test
    void testDoFilter_InvalidTokenFormat() throws IOException {
        when(httpRequest.getHeader("Authorization")).thenReturn("InvalidHeader");
        jwtFilter.doFilter(httpRequest, httpResponse, filterChain);
        verify(httpResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
        verifyNoInteractions(filterChain);
    }
}
