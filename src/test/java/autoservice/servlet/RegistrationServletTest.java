package autoservice.servlet;

import autoservice.adapter.service.UserAuthService;
import autoservice.model.Role;
import autoservice.model.User;
import autoservice.servlet.dto.RegistrationUserDTO;
import autoservice.servlet.dtomappers.RegistrationUserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static org.mockito.Mockito.*;

class RegistrationServletTest {
    private RegistrationServlet registrationServlet;
    private UserAuthService userAuthService;
    private ObjectMapper objectMapper;
    private RegistrationUserMapper userMapper;

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
        userMapper = mock(RegistrationUserMapper.class);
        objectMapper = new ObjectMapper();
        registrationServlet = new RegistrationServlet();
        registrationServlet.setUserAuthService(userAuthService);
        registrationServlet.setUserMapper(userMapper);

        doNothing().when(writer).write(anyString());
        when(resp.getWriter()).thenReturn(writer);
    }

    @Test
    void testDoPost_SuccessfulRegistration() throws Exception {
        RegistrationUserDTO registrationDTO = new RegistrationUserDTO();
        registrationDTO.setUsername("newUser");
        registrationDTO.setPassword("password");
        registrationDTO.setName("John");
        registrationDTO.setSurname("Doe");
        registrationDTO.setPhone("1234567890");

        User user = new User(Role.CLIENT, "newUser", "hashedPassword", "John", "Doe", "1234567890");

        when(userMapper.userDTOToUser(registrationDTO)).thenReturn(user);
        when(userAuthService.register(user)).thenReturn(Optional.of(user));

        byte[] registrationDTOBytes = objectMapper.writeValueAsBytes(registrationDTO);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(registrationDTOBytes);
        ServletInputStream servletInputStream = new MockServletInputStream(byteArrayInputStream);
        when(req.getInputStream()).thenReturn(servletInputStream);

        registrationServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_CREATED);
        verify(writer).write("User registered successfully.");
    }

    @Test
    void testDoPost_MissingParameters() throws Exception {
        RegistrationUserDTO registrationDTO = new RegistrationUserDTO();
        registrationDTO.setUsername("");
        registrationDTO.setPassword("");
        registrationDTO.setName("");
        registrationDTO.setSurname("");
        registrationDTO.setPhone("");

        byte[] registrationDTOBytes = objectMapper.writeValueAsBytes(registrationDTO);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(registrationDTOBytes);
        ServletInputStream servletInputStream = new MockServletInputStream(byteArrayInputStream);
        when(req.getInputStream()).thenReturn(servletInputStream);

        registrationServlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters.");
    }

    @Test
    void testDoPost_UsernameAlreadyExists() throws Exception {
        RegistrationUserDTO registrationDTO = new RegistrationUserDTO();
        registrationDTO.setUsername("existingUser");
        registrationDTO.setPassword("password");
        registrationDTO.setName("John");
        registrationDTO.setSurname("Doe");
        registrationDTO.setPhone("1234567890");

        User user = new User(Role.CLIENT, "existingUser", "hashedPassword", "John", "Doe", "1234567890");

        when(userMapper.userDTOToUser(registrationDTO)).thenReturn(user);
        when(userAuthService.register(user)).thenReturn(Optional.empty());

        byte[] registrationDTOBytes = objectMapper.writeValueAsBytes(registrationDTO);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(registrationDTOBytes);
        ServletInputStream servletInputStream = new MockServletInputStream(byteArrayInputStream);
        when(req.getInputStream()).thenReturn(servletInputStream);

        registrationServlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "The username you sent is busy!");
    }
}
