package autoservice.adapter.service;

import autoservice.model.Car;
import autoservice.model.CarState;
import autoservice.model.Role;
import autoservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
    boolean add(Role role, Car car) throws RoleException;

    /**
     * Deletes a {@link Car} from the service.
     *
     * @param car the car to be deleted
     * @return {@code true} if the car was successfully deleted, {@code false} otherwise
     */

    boolean delete(Role role, Car car) throws RoleException;

    /**
     * Retrieves a {@link Car} that matches the given filter.
     *
     * @param predicate a predicate used to filter cars
     * @return an {@link Optional} containing the car if found, or an empty {@link Optional} if not
     */
    Optional<Car> getCarByFilter(Role role,Predicate<Car> predicate);

    /**
     * Retrieves a {@link Car} by its ID.
     *
     * @param id the ID of the car
     * @return an {@link Optional} containing the car if found, or an empty {@link Optional} if not
     */
    Optional<Car> getById(Role role,int id);

    /**
     * Retrieves all {@link Car} objects.
     *
     * @return a list of all cars
     */
    List<Car> getAllCar(Role role);
    Stream<Car> getAllCarStream(Role role);

    /**
     * Retrieves {@link Car} objects that match the given filter.
     *
     * @param predicate a predicate used to filter cars
     * @return a list of cars that match the filter
     */
    List<Car> getCarsByFilter(Role role,Predicate<Car> predicate);
    Stream<Car> getCarsByFilterStream(Role role,Predicate<Car> predicate);

    void editCar(Role role, Car car) throws RoleException;

    void changeStatus(Role role, Car car, CarState newState) throws RoleException;
}


