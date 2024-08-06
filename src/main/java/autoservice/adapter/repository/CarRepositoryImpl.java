package autoservice.adapter.repository;

import autoservice.core.model.Car;
import autoservice.core.port.CarRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
/**
 * Implementation of the {@link CarRepository} interface.
 * This class provides basic CRUD operations for {@link Car} objects using an in-memory list.
 */
public class CarRepositoryImpl implements CarRepository {

    private final List<Car> cars = new ArrayList<>();
    /**
     * Adds a new {@link Car} to the repository.
     *
     * @param object the car to be added
     * @return {@code true} if the car was added successfully, {@code false} otherwise
     */
    @Override
    public boolean create(Car object) {
        return cars.add(object);
    }
    /**
     * Removes a {@link Car} from the repository.
     *
     * @param object the car to be removed
     * @return {@code true} if the car was removed successfully, {@code false} otherwise
     */

    @Override
    public boolean delete(Car object) {
        return cars.remove(object);
    }
    /**
     * Retrieves all cars from the repository.
     *
     * @return a list of all cars
     */
    @Override
    public List<Car> findAll() {
        return cars;
    }
    /**
     * Finds cars in the repository that match the given filter.
     *
     * @param predicate the filter to apply to the cars
     * @return a stream of cars that match the filter
     */
    @Override
    public Stream<Car> findByFilter(Predicate<Car> predicate) {
        return cars.stream().filter(predicate);
    }
}
