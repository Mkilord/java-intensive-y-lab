package autoservice.model;

import java.time.LocalDate;

public class ServiceOrder extends Order {

    public ServiceOrder(int id, LocalDate date, OrderStatus status, User customer, Car car) {
        super(id, date, status, customer, car);
    }

    public ServiceOrder(LocalDate date, OrderStatus status, User customer, Car car) {
        super(date, status, customer, car);
    }

    public ServiceOrder(OrderStatus status, User customer, Car car) {
        super(status, customer, car);
    }

    public ServiceOrder(User customer, Car car) {
        super(customer, car);
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
