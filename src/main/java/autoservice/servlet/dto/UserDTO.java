package autoservice.servlet.dto;

import autoservice.model.Role;
import lombok.Data;

@Data
public class UserDTO {
    private int id;
    private Role role;
    private String username;
    private String name;
    private String surname;
    private String phone;
}