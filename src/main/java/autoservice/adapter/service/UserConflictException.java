package autoservice.adapter.service;

public class UserConflictException extends RuntimeException{
    public UserConflictException(String message) {
        super(message);
    }
}
