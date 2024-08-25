package autoservice.domen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Ответ, содержащий данные о пользователе")
public class UserResponse {
    @Schema(description = "Идентификатор пользователя")
    int id;
    @Schema(description = "Никнейм пользователя")
    String username;
    @Schema(description = "Имя пользователя")
    String name;
    @Schema(description = "Фамилия пользователя")
    String surname;
    @Schema(description = "Телефон пользователя")
    String phone;
    @Schema(description = "Email пользователя")
    String email;
}