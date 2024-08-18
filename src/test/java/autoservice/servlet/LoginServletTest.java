package autoservice.servlet;

import autoservice.adapter.service.UserAuthService;
import autoservice.model.Role;
import autoservice.model.User;
import autoservice.servlet.dto.LoginDTO;
import autoservice.servlet.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LoginServletTest {
    private LoginServlet loginServlet;
    private UserAuthService userAuthService;
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        userAuthService = mock(UserAuthService.class);
        objectMapper = new ObjectMapper();
        loginServlet = new LoginServlet();
        loginServlet.setUserAuthService(userAuthService);

        when(resp.getWriter()).thenReturn(writer);
    }

    @Test
    void testDoPost_ValidCredentials() throws Exception {
        String username = "user";
        String password = "password";
        String token = "dummyToken";
        User user = new User(Role.CLIENT, username, password, "name", "surname", "93943949394");

        try (var mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.generateToken(user)).thenReturn(token);

            when(userAuthService.login(username, password)).thenReturn(Optional.of(user));

            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUsername(username);
            loginDTO.setPassword(password);

            byte[] loginDTOBytes = objectMapper.writeValueAsBytes(loginDTO);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(loginDTOBytes);
            ServletInputStream servletInputStream = new MockServletInputStream(byteArrayInputStream);
            when(req.getInputStream()).thenReturn(servletInputStream);

            loginServlet.doPost(req, resp);

            verify(resp).setContentType("application/json");

            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            verify(writer).write(captor.capture());
            assertEquals("{\"token\":\"" + token + "\"}", captor.getValue());
        }
    }

    @Test
    void testDoPost_InvalidCredentials() throws Exception {
        String username = "user";
        String password = "password";

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(password);

        byte[] loginDTOBytes = objectMapper.writeValueAsBytes(loginDTO);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(loginDTOBytes);
        ServletInputStream servletInputStream = new MockServletInputStream(byteArrayInputStream);
        when(req.getInputStream()).thenReturn(servletInputStream);

        doNothing().when(resp).sendError(HttpServletResponse.SC_CONFLICT, "Invalid username or password.");
        when(userAuthService.login(username, password)).thenReturn(Optional.empty());

        loginServlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_CONFLICT, "Invalid username or password.");
    }

    @Test
    void testDoPost_MissingParameters() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("");
        loginDTO.setPassword("");

        byte[] loginDTOBytes = objectMapper.writeValueAsBytes(loginDTO);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(loginDTOBytes);
        ServletInputStream servletInputStream = new MockServletInputStream(byteArrayInputStream);
        when(req.getInputStream()).thenReturn(servletInputStream);

        doNothing().when(resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters.");

        loginServlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters.");
    }

}
