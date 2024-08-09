package autoservice.model;

public enum Role {
    CLIENT("Client"),
    MANAGER("Manager"),
    ADMIN("Admin");

    private final String text;

    Role(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
