package autoservice.model;

import java.time.LocalDate;
import java.util.Date;

public class ServiceOrder extends Order {
    public ServiceOrder(User customer, Car car) {
        super(customer, car);
    }

    public ServiceOrder(int id, User customer, Car car, OrderStatus status, LocalDate date) {
        super(id, customer, car, status, date);
    }


    @Override
    public String getView() {
        return String.format("Service order. Number: %d\n" +
                "Date: %s" +
                "State: %s;\n" +
                "Customer: \n"
                + customer.getView() + "\n" +
                "Car:\n"
                + car.getView(), id, date, status);
    }
}
