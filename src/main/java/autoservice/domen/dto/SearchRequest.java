package autoservice.domen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на поиск")
public class SearchRequest {

    @Schema(description = "Строка поиска", example = "Jon")
    @Size(min = 5, max = 50, message = "Строка поиска должна содержать от 5 до 50 символов.")
    @NotBlank(message = "Строка поиска не может быть пустой.")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ0-9 ]+$", message = "Строка поиска должна содержать только буквы, цифры и пробелы.")
    private String searchSource;
}
