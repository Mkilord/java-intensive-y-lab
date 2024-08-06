package autoservice.core.model;

public enum CarState {
    FOR_SALE,
    SOLD{
        @Override
        public String toString() {
            return "Sold status";
        }
    },
    NOT_SALE{
        @Override
        public String toString() {
            return "Not sale status";
        }
    },
    FOR_SERVICE{
        @Override
        public String toString() {
            return "Service Status";
        }
    }
}
