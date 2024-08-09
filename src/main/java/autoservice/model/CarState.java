package autoservice.model;

public enum CarState {
    FOR_SALE("For sale"),
    SOLD("Sold"),
    NOT_SALE("Not sale"),
    FOR_SERVICE("For service");
    private final String text;

    CarState(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
