package autoservice.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.time.LocalDate;

import static autoservice.model.OrderStatus.IN_PROGRESS;

@Data
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@AllArgsConstructor()
public abstract class Order implements View {
    @NonFinal
    int id;
    LocalDate date;
    @NonFinal
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
