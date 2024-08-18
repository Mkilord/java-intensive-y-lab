package autoservice.adapter.service;

public class RoleException extends Exception {
    public static final String PERMISSION_ERROR_MSG = "There are not enough permissions to perform this action!";

    public RoleException(String message) {
        super(message);
    }
}
