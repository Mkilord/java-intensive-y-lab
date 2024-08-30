package autoservice.adapter.service;

public class NotFoundException extends RuntimeException {
    public static final String MSG = "Not found!";

    public NotFoundException(String message) {
        super(message);
    }
}
