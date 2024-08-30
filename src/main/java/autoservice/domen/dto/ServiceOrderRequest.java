package autoservice.domen.dto;

import autoservice.domen.model.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ServiceOrderRequest {

    @NotNull(message = "Дата заказа не может быть null")
    @Schema(description = "Дата заказа", example = "2024-08-25")
    private LocalDate date;

    @NotNull(message = "Статус заказа не может быть null")
    @Schema(description = "Статус заказа", example = "PENDING")
    private OrderStatus status;

    @NotNull(message = "ID клиента не может быть null")
    @Schema(description = "ID клиента", example = "123")
    private int customerId;

    @NotNull(message = "ID автомобиля не может быть null")
    @Schema(description = "ID автомобиля", example = "456")
    private int carId;
}
