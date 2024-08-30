package autoservice.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.time.LocalDate;

import static autoservice.model.OrderStatus.IN_PROGRESS;

@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
@AllArgsConstructor()
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public abstract class Order implements View {
    int id;
    LocalDate date;
    OrderStatus status;
    User customer;
    Car car;

    public Order(LocalDate date, OrderStatus status, User customer, Car car) {
        this(0, date, status, customer, car);
    }

    public Order(OrderStatus status, User customer, Car car) {
        this(LocalDate.now(), status, customer, car);
    }

    public Order(User customer, Car car) {
        this(IN_PROGRESS, customer, car);
    }
}
