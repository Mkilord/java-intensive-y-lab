package autoservice.domen.dto;

import autoservice.domen.model.enums.CarState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Schema(description = "Запрос машины")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarRequest {
    @Schema(description = "Марка автомобиля", example = "Toyota")
    @NotBlank(message = "Марка не может быть пустой")
    String make;

    @Schema(description = "Модель автомобиля", example = "Corolla")
    @NotBlank(message = "Модель не может быть пустой")
    String model;

    @Schema(description = "Год выпуска автомобиля", example = "2021")
    @NotNull(message = "Год не может быть пустым")
    @Positive(message = "Год должен быть положительным числом")
    Integer year;

    @Schema(description = "Цена автомобиля", example = "20000")
    @NotNull(message = "Цена не может быть пустой")
    @Positive(message = "Цена должна быть положительным числом")
    Long price;

    @Schema(description = "Состояние автомобиля", example = "NEW")
    @NotNull(message = "Состояние не может быть пустым")
    CarState state;
}
