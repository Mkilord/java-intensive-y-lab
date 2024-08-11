package autoservice.adapter.service;

import autoservice.model.Car;
import autoservice.model.CarState;
import autoservice.model.Role;
import autoservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Service interface for managing {@link Car} objects.
 * <p>
 * Provides methods for adding, deleting, retrieving, and updating the state of {@link Car} objects.
 *
 * @see Car
 * @see User
 */
public interface CarService {
    /**
     * Adds a new {@link Car} to the service.
     *
     * @param car the car to be added
     * @return {@code true} if the car was successfully added, {@code false} otherwise
     */
    boolean add(Car car);

    /**
     * Deletes a {@link Car} from the service.
     *
     * @param car the car to be deleted
     * @return {@code true} if the car was successfully deleted, {@code false} otherwise
     */

    boolean delete(Car car);

    /**
     * Retrieves a {@link Car} that matches the given filter.
     *
     * @param predicate a predicate used to filter cars
     * @return an {@link Optional} containing the car if found, or an empty {@link Optional} if not
     */
    Optional<Car> getCarByFilter(Predicate<Car> predicate);

    /**
     * Retrieves a {@link Car} by its ID.
     *
     * @param id the ID of the car
     * @return an {@link Optional} containing the car if found, or an empty {@link Optional} if not
     */
    Optional<Car> getById(int id);

    /**
     * Retrieves all {@link Car} objects.
     *
     * @return a list of all cars
     */
    List<Car> getAllCar();

    /**
     * Retrieves {@link Car} objects that match the given filter.
     *
     * @param predicate a predicate used to filter cars
     * @return a list of cars that match the filter
     */
    List<Car> getCarsByFilter(Predicate<Car> predicate);

    void editCar(Car car);
    void changeStatus(Car car, User user, CarState newState);
    /**
     * Marks a {@link Car} as available for sale.
     * <p>
     * Only users with roles other than {@link Role#CLIENT} can perform this action.
     *
     * @param car  the car to be marked for sale
     * @param user the user performing the action
     */

    @Deprecated
    void markForSale(Car car, User user);

    /**
     * Marks a {@link Car} as sold.
     * <p>
     * Only users with roles other than {@link Role#CLIENT} can perform this action.
     *
     * @param car  the car to be marked as sold
     * @param user the user performing the action
     */
    @Deprecated
    void markForSold(Car car, User user);

    /**
     * Marks a {@link Car} as not for sale.
     * <p>
     * Only users with roles other than {@link Role#CLIENT} can perform this action.
     *
     * @param car  the car to be marked as not for sale
     * @param user the user performing the action
     */
    @Deprecated
    void markForNotSale(Car car, User user);

    /**
     * Marks a {@link Car} as needing service.
     * <p>
     * Only users with roles other than {@link Role#CLIENT} can perform this action.
     *
     * @param car  the car to be marked for service
     * @param user the user performing the action
     */
    @Deprecated
    void markForService(Car car, User user);

}


