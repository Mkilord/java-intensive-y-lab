package autoservice.domen.dto;

import autoservice.domen.model.enums.CarState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarResponse {
    @Schema(description = "Идентификатор автомобиля", example = "1")
    int id;
    @Schema(description = "Состояние автомобиля", example = "NEW")
    CarState state;
    @Schema(description = "Марка автомобиля", example = "Toyota")
    String make;
    @Schema(description = "Модель автомобиля", example = "Corolla")
    String model;
    @Schema(description = "Год выпуска автомобиля", example = "2021")
    int year;
    @Schema(description = "Цена автомобиля", example = "20000")
    long price;
}
