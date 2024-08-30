package autoservice.servlet.utils;

import autoservice.model.Role;
import autoservice.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtUtilTest {

    @Test
    void generateToken() {
        var jwtToken = JwtUtil.generateToken(new User(Role.CLIENT, "username", "dfkjdfk"));
        Assertions.assertFalse(Objects.isNull(jwtToken),"Should be is not null!");
    }

    @Test
    void validateToken() {
        var token = JwtUtil.generateToken(new User(Role.CLIENT, "username", "dfkjdfk"));

        var out = JwtUtil.validateToken(token);

        assertEquals("username", out.getSubject(),"Should be username!");
        assertEquals(Role.CLIENT.name(), out.get("role"), "Should be CLIENT!");
    }
}