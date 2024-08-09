package autoservice.model;

public enum OrderStatus {
    IN_PROGRESS("In progress"),
    COMPLETE("Complete"),
    CANCEL("Canceled");

    private final String text;

    OrderStatus(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
