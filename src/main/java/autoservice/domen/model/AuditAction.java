package autoservice.domen.model;

public enum AuditAction {
    LOG_IN("Log in:"),
    REGISTER("Register:"),
    EDIT_CAR("Edit car:"),
    ADD_CAR("Add car:"),
    EDIT_USER("Edit user:"),
    CHANGE_ORDER_STATUS("Change order status:"),
    CHANGE_CAR_STATUS("Change car status:"),
    CHANGE_ROLE("Change role:"),
    DELETE_CAR("Delete car:"),
    CREATE_ORDER("Create order:"),
    CANCEL_ORDER("Cancel order:");
    private final String text;

    AuditAction(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
