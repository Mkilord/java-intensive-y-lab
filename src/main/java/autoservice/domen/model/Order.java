package autoservice.domen.model;

import autoservice.domen.model.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
@AllArgsConstructor()
public abstract class Order {
    int id;
    LocalDate date;
    OrderStatus status;
    User customer;
    Car car;
}
