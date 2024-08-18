package autoservice.servlet;

import autoservice.adapter.repository.impl.UserRepositoryImpl;
import autoservice.adapter.service.UserAuthService;
import autoservice.adapter.service.impl.UserAuthServiceImpl;
import autoservice.model.User;
import autoservice.servlet.dto.LoginDTO;
import autoservice.servlet.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import java.io.IOException;

@WebServlet("/login")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginServlet extends HttpServlet {
    final ObjectMapper objectMapper = new ObjectMapper();
    @Setter
    UserAuthService userAuthService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userAuthService = new UserAuthServiceImpl(new UserRepositoryImpl());
    }
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var loginDTO = objectMapper.readValue(req.getInputStream(), LoginDTO.class);

        if (loginDTO.getUsername().isBlank()
                || loginDTO.getPassword().isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters.");
            return;
        }


        var userOpt = userAuthService.login(loginDTO.getUsername(), loginDTO.getPassword());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = JwtUtil.generateToken(user);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"token\":\"" + token + "\"}");
        } else {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Invalid username or password.");
        }
    }
}
