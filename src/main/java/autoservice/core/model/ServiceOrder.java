package autoservice.core.model;

public class ServiceOrder extends Order {
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
