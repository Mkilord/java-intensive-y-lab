package autoservice.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@EqualsAndHashCode
@ToString(includeFieldNames = false)
@Getter
public abstract class Order implements View {
    @Setter
    protected int id;
    protected final User customer;
    protected final Car car;
    protected final Date date = new Date();
    @Setter
    protected OrderStatus status = OrderStatus.IN_PROGRESS;

    public Order(User customer, Car car) {
        this.car = car;
        this.customer = customer;
    }
}
