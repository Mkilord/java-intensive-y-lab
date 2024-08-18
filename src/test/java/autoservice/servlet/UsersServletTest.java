package autoservice.servlet;

import autoservice.adapter.service.RoleException;
import autoservice.adapter.service.UserService;
import autoservice.model.Role;
import autoservice.servlet.dto.UserDTO;
import autoservice.servlet.dtomappers.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

public class UsersServletTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Claims claims;

    @InjectMocks
    private UsersServlet usersServlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private PrintWriter writer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        writer = new PrintWriter(responseWriter);
        objectMapper = new ObjectMapper();
        usersServlet.setUserMapper(userMapper);
        usersServlet.setUserService(userService);
    }


    @Test
    void testDoPostUpdateActionAsClient() throws Exception {
        when(request.getParameter("action")).thenReturn("update");
        when(request.getAttribute("userClaims")).thenReturn(claims);
        when(claims.get("role")).thenReturn(Role.CLIENT);
        when(response.getWriter()).thenReturn(writer);

        var userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setRole(Role.MANAGER);
        userDTO.setName("Test");
        userDTO.setSurname("User");
        userDTO.setPhone("+1234567890");

        byte[] data = objectMapper.writeValueAsBytes(userDTO);

        when(request.getInputStream()).thenReturn(new CustomServletInputStream(data));

        usersServlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, RoleException.PERMISSION_ERROR_MSG);
    }

    @Test
    void testDoPostInvalidAction() throws Exception {
        when(request.getParameter("action")).thenReturn("invalid");
        when(request.getAttribute("userClaims")).thenReturn(claims);
        when(claims.get("role")).thenReturn(Role.ADMIN);
        when(response.getWriter()).thenReturn(writer);

        usersServlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
    }


    public static class CustomServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream byteArrayInputStream;

        public CustomServletInputStream(byte[] data) {
            this.byteArrayInputStream = new ByteArrayInputStream(data);
        }

        @Override
        public int read() {
            return byteArrayInputStream.read();
        }

        @Override
        public void close() throws IOException {
            byteArrayInputStream.close();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }
}
