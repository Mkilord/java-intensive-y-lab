package autoservice.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@EqualsAndHashCode
@ToString(includeFieldNames = false)
@Getter
public abstract class Order implements View {
    @Setter
    protected int id;
    protected final User customer;
    protected final Car car;
    protected LocalDate date;
    @Setter
    protected OrderStatus status = OrderStatus.IN_PROGRESS;

    public Order(int id, User customer, Car car, OrderStatus status, LocalDate date) {
        this.id = id;
        this.customer = customer;
        this.car = car;
        this.status = status;
        this.date = date;
    }

    public Order(User customer, Car car) {
        this.date = LocalDate.now();
        this.car = car;
        this.customer = customer;
    }

}
