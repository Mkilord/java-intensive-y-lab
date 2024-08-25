package autoservice.domen.dto;

import autoservice.domen.model.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ServiceOrderResponse {

    @Schema(description = "ID заказа", example = "1")
    private int id;

    @Schema(description = "Дата заказа", example = "2024-08-25")
    private LocalDate date;

    @Schema(description = "Статус заказа", example = "COMPLETE")
    private OrderStatus status;

    @Schema(description = "ID клиента", example = "123")
    private int customerId;

    @Schema(description = "ID автомобиля", example = "456")
    private int carId;
}
