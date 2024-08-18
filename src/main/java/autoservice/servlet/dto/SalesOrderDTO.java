package autoservice.servlet.dto;

import autoservice.model.OrderStatus;
import lombok.Data;

import java.util.Date;

@Data
public class SalesOrderDTO {
    private int id;
    private Date data;
    private int carId;
    private int customerId;
    private OrderStatus status;
}
