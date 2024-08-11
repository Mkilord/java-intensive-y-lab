package autoservice.adapter.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Generic repository interface for performing CRUD (Create, Read, Update, Delete) operations.
 * <p>
 * This interface defines methods for creating, deleting, retrieving, and filtering objects of type {@code T}.
 *
 * @param <T> the type of objects managed by this repository
 */
public interface CRUDRepository<T> {

    static void getSQLError(Exception e) {
        System.err.println("SQL Error: " + e.getMessage());
    }
    /**
     * Creates a new object in the repository.
     *
     * @param object the object to be created
     * @return {@code true} if the object was successfully created, {@code false} otherwise
     */
    boolean create(T object);

    /**
     * Deletes an object from the repository.
     *
     * @param object the object to be deleted
     * @return {@code true} if the object was successfully deleted, {@code false} otherwise
     */
    boolean delete(T object);

    void update(T object);

    Optional<T> findById(int id);

    /**
     * Retrieves all objects from the repository.
     *
     * @return a list of all objects
     */
    List<T> findAll();

    /**
     * Finds objects in the repository that match the given filter.
     *
     * @param predicate a predicate used to filter objects
     * @return a stream of objects that match the filter
     */
    Stream<T> findByFilter(Predicate<T> predicate);
}
