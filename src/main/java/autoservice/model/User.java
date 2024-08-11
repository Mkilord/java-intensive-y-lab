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
    private int id;
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

    public User(int id, Role role, String username, String password, String name, String surname, String phone) {
        this.id = id;
        this.role = role;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
    }

    @Override
    public String getView() {
        return String.format("Id: %d,Username: %s; Name: %s; Surname: %s; Phone: %s;", id,
                username, name, surname, phone);
    }

}
