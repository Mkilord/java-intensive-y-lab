package autoservice.core.model;

public class SalesOrder extends Order {
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
