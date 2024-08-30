package autoservice.servlet.dto;

import autoservice.model.OrderStatus;
import lombok.Data;

import java.util.Date;

@Data
public class ServiceOrderDTO {
    private int id;
    private int carId;
    private int customerId;
    private Date date;
    private OrderStatus status;
}
