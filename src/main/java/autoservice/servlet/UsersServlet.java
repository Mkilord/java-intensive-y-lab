package autoservice.servlet;

import autoservice.adapter.repository.impl.UserRepositoryImpl;
import autoservice.adapter.service.RoleException;
import autoservice.adapter.service.UserService;
import autoservice.adapter.service.impl.UserServiceImpl;
import autoservice.model.Role;
import autoservice.model.User;
import autoservice.servlet.dto.UserDTO;
import autoservice.servlet.dtomappers.UserMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Cleanup;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/users")
public class UsersServlet extends HttpServlet {
    @Setter
    private UserService userService;
    @Setter
    private UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    public void init(ServletConfig config) {
        this.userService = new UserServiceImpl(new UserRepositoryImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var role = getRole(req);
        if (role.equals(Role.CLIENT)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, RoleException.PERMISSION_ERROR_MSG);
            return;
        }

        resp.setContentType("application/json");
        var action = req.getParameter("action");

        if ("list".equals(action)) {
            try {
                var userDTOs = userService.getAllUsersStream(role).map(userMapper::toUserDTO).toList();
                @Cleanup var writer = resp.getWriter();
                writer.write(toJson(userDTOs));
            } catch (RoleException e) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, RoleException.PERMISSION_ERROR_MSG);
            }
        } else if ("search".equals(action)) {
            String searchString = req.getParameter("searchString");
            try {
                var users = userService.getAllUsers(role);
                var userDTOs = UserServiceImpl.filterUsersByString(users, searchString).stream().map(userMapper::toUserDTO).toList();
                @Cleanup var writer = resp.getWriter();
                writer.write(toJson(userDTOs));
            } catch (RoleException e) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, RoleException.PERMISSION_ERROR_MSG);
            }

        } else if ("getById".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));

            try {
                var userOpt = userService.getUserByID(role, id);
                if (userOpt.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }
                @Cleanup var writer = resp.getWriter();
                writer.write(userOpt.map(userMapper::toUserDTO).map(this::toJson).orElse("{}"));
            } catch (RoleException e) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, RoleException.PERMISSION_ERROR_MSG);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    private Role getRole(HttpServletRequest req) {
        var userClaims = (Claims) req.getAttribute("userClaims");
        return (Role) userClaims.get("role");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var action = req.getParameter("action");

        var role = getRole(req);
        if (role.equals(Role.CLIENT)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, RoleException.PERMISSION_ERROR_MSG);
            return;
        }

        if ("update".equals(action)) {
            var objectMapper = new ObjectMapper();
            var userDTO = objectMapper.readValue(req.getReader(), UserDTO.class);

            var errors = validateUserDTO(userDTO);
            if (!errors.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                @Cleanup var writer = resp.getWriter();
                writer.write("{\"status\": \"error\", \"messages\": \"" + String.join("; ", errors) + "\"}");
                return;
            }

            User user = userMapper.toUser(userDTO);

            try {
                userService.editUser(role, user);
            } catch (RoleException e) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, RoleException.PERMISSION_ERROR_MSG);
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    private List<String> validateUserDTO(UserDTO userDTO) {
        var errors = new ArrayList<String>();

        if (userDTO.getRole() == null) {
            errors.add("Role cannot be null");
        }


        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
            errors.add("Username cannot be blank");
        } else if (userDTO.getUsername().length() < 3 || userDTO.getUsername().length() > 20) {
            errors.add("Username must be between 3 and 20 characters");
        }

        if (userDTO.getName() == null || userDTO.getName().trim().isEmpty()) {
            errors.add("Name cannot be blank");
        } else if (userDTO.getName().isEmpty() || userDTO.getName().length() > 50) {
            errors.add("Name must be between 1 and 50 characters");
        }

        if (userDTO.getSurname() == null || userDTO.getSurname().trim().isEmpty()) {
            errors.add("Surname cannot be blank");
        } else if (userDTO.getSurname().isEmpty() || userDTO.getSurname().length() > 50) {
            errors.add("Surname must be between 1 and 50 characters");
        }

        if (userDTO.getPhone() == null || !userDTO.getPhone().matches("^\\+?[0-9]{10,15}$")) {
            errors.add("Phone number must be valid");
        }

        return errors;
    }

    @SneakyThrows(JsonProcessingException.class)
    private String toJson(Object object) {
        var mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}

