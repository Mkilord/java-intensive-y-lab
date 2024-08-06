package autoservice.core.model;

public enum Role {
    CLIENT{
        @Override
        public String toString() {
            return "Client";
        }
    },
    MANAGER{
        @Override
        public String toString() {
            return "Manager";
        }
    },
    ADMIN{
        @Override
        public String toString() {
            return "Admin";
        }
    }
}
