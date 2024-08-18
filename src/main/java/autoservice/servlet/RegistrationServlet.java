package autoservice.servlet;

import autoservice.adapter.repository.impl.UserRepositoryImpl;
import autoservice.adapter.service.UserAuthService;
import autoservice.adapter.service.impl.UserAuthServiceImpl;
import autoservice.servlet.dto.RegistrationUserDTO;
import autoservice.servlet.dtomappers.RegistrationUserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.IOException;

@WebServlet("/register")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationServlet extends HttpServlet {
    final ObjectMapper objectMapper = new ObjectMapper();
    @Setter
    RegistrationUserMapper userMapper = RegistrationUserMapper.INSTANCE;
    @Setter
    UserAuthService userAuthService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userAuthService = new UserAuthServiceImpl(new UserRepositoryImpl());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var userDTO = objectMapper.readValue(req.getInputStream(), RegistrationUserDTO.class);

        if (userDTO.getUsername().isBlank()
                || userDTO.getPassword().isBlank()
                || userDTO.getName().isBlank()
                || userDTO.getSurname().isBlank()
                || userDTO.getPhone().isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters.");
            return;
        }
        userDTO.setPassword(hashPassword(userDTO.getPassword()));

        var newUser = userMapper.userDTOToUser(userDTO);

        var newUserOpt = userAuthService.register(newUser);
        if (newUserOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "The username you sent is busy!");
        }
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write("User registered successfully.");
    }

    private String hashPassword(String password) {
        // Хеширование пароля (пример с использованием SHA-256)
//        return org.apache.commons.codec.digest.DigestUtils.sha256Hex(password);
        return password;
    }

}
