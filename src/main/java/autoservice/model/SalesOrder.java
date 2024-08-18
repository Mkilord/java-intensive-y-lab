package autoservice.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class SalesOrder extends Order {

    public SalesOrder(int id, LocalDate date, OrderStatus status, User customer, Car car) {
        super(id, date, status, customer, car);
    }

    public SalesOrder(LocalDate date, OrderStatus status, User customer, Car car) {
        super(date, status, customer, car);
    }

    public SalesOrder(OrderStatus status, User customer, Car car) {
        super(status, customer, car);
    }

    public SalesOrder(User customer, Car car) {
        super(customer, car);
    }

    @Override
    public String getView() {
        return String.format("Order number: %d\n" +
                "Date: %s\n" +
                "State: %s;\n" +
                "Customer: \n"
                + customer.getView() + "\n" +
                "Car:\n"
                + car.getView(), id, date, status);
    }
}
