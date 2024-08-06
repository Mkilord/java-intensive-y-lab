package autoservice.core.model;
/**
 * Interface for classes that provide a string representation for viewing.
 * <p>
 * Implementing classes should define how their data is represented as a string.
 */
public interface View {

    /**
     * Returns the string representation of the object for viewing.
     *
     * @return the string representation of the object
     */
    String getView();
}
