package autoservice.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class User implements View {

    int id;
    Role role;
    String username;
    String password;
    String name;
    String surname;
    String phone;

    public User(Role role, String username, String password) {
        this(0, role, username, password, null, null, null);
    }

    public User(Role role, String username, String password, String name, String surname, String phone) {
        this(0, role, username, password, name, surname, phone);
    }

    @Override
    public String getView() {
        return String.format("Id: %d,Username: %s; Name: %s; Surname: %s; Phone: %s;", id,
                username, name, surname, phone);
    }
}
