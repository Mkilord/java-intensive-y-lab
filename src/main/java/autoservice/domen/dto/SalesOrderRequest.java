package autoservice.domen.dto;

import autoservice.domen.model.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.util.Date;

@Data
public class SalesOrderRequest {

    @Schema(description = "Дата заказа", example = "2024-08-25")
    @NotNull(message = "Дата заказа не может быть пустой")
    @PastOrPresent(message = "Дата заказа должна быть в прошлом или настоящем")
    private Date date;

    @Schema(description = "Статус заказа", example = "COMPLETE")
    @NotNull(message = "Статус заказа не может быть пустым")
    private OrderStatus status;

    @Schema(description = "ID клиента", example = "1")
    @NotNull(message = "ID клиента не может быть пустым")
    private Integer customerId;

    @Schema(description = "ID автомобиля", example = "1")
    @NotNull(message = "ID автомобиля не может быть пустым")
    private Integer carId;
}

