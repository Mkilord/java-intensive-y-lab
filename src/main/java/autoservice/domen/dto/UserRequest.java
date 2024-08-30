package autoservice.domen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    @Schema(description = "Имя пользователя для входа в систему", example = "ivanivanov", required = true)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 4, max = 20, message = "Имя пользователя должно содержать от 4 до 20 символов")
    private String username;

    @Schema(description = "Имя пользователя", example = "Иван", required = true)
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    private String name;

    @Schema(description = "Фамилия пользователя", example = "Иванов", required = true)
    @NotBlank(message = "Фамилия не может быть пустой")
    @Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов")
    private String surname;

    @Schema(description = "Email пользователя", example = "ivan.ivanov@example.com", required = true)
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @Schema(description = "Номер телефона пользователя", example = "+1234567890")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Неверный формат номера телефона")
    private String phone;
}
