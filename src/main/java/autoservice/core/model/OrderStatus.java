package autoservice.core.model;

public enum OrderStatus {
    IN_PROCESS {
        @Override
        public String toString() {
            return "In progress";
        }
    },
    COMPLETE() {
        @Override
        public String toString() {
            return "Complete";
        }
    },
    CANCEL() {
        @Override
        public String toString() {
            return "Canceled";
        }
    }
}
