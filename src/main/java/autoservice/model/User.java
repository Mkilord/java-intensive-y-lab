package autoservice.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString(includeFieldNames = false)
@EqualsAndHashCode

public class User implements View {
    @Setter
    private Role role;
    private final String username;
    @Setter
    private String password;
    @Setter
    private String name;
    @Setter
    private String surname;
    @Setter
    private String phone;

    public User(Role role, String username, String password) {
        this.role = role;
        this.username = username;
        this.password = password;
    }

    @Override
    public String getView() {
        return String.format("Username: %s; Name: %s; Surname: %s; Phone: %s;",
                username, name, surname, phone);
    }

}
