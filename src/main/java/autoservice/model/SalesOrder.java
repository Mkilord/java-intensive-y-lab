package autoservice.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class SalesOrder extends Order {
    public SalesOrder(User customer, Car car) {
        super(customer, car);
    }

    public SalesOrder(int id, User customer, Car car, OrderStatus status, LocalDate date) {
        super(id, customer, car, status, date);
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
