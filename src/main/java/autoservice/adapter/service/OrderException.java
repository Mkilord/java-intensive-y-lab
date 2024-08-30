package autoservice.adapter.service;

public class OrderException extends Exception {
    public static final String INVALID_CAR_ERROR_MSG = "You can't put this car in order!";

    public OrderException(String message) {
        super(message);
    }
}
