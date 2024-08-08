package autoservice.adapter.service;

import autoservice.core.model.Car;
import autoservice.core.model.CarState;
import autoservice.core.model.Role;
import autoservice.core.model.User;
import autoservice.core.port.CarRepository;
import autoservice.core.services.CarService;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link CarService} interface.
 */
public class CarServiceImpl implements CarService {
    private final CarRepository carRepo;

    /**
     * Constructs a new {@code CarServiceImpl} with the specified {@link CarRepository}.
     *
     * @param carRepo the repository used for car data
     */
    public CarServiceImpl(CarRepository carRepo) {
        this.carRepo = carRepo;
    }

    /**
     * Filters a list of cars by a specified search string.
     *
     * @param cars         the list of cars to filter
     * @param searchString the search string to filter by
     * @return a list of cars that match the search string
     */
    public static List<Car> filterCarsByString(List<Car> cars, String searchString) {
        return cars.stream()
                .filter(car -> {
                    boolean matchesId = String.valueOf(car.getId()).equals(searchString);
                    boolean matchesMake = car.getMake().equalsIgnoreCase(searchString);
                    boolean matchesModel = car.getModel().equalsIgnoreCase(searchString);
                    boolean matchesYear = String.valueOf(car.getYear()).equals(searchString);
                    boolean matchesPrice = String.valueOf(car.getPrice()).equals(searchString);
                    boolean matchesState = car.getState().toString().equalsIgnoreCase(searchString);
                    return matchesId || matchesMake || matchesModel || matchesYear || matchesPrice || matchesState;
                })
                .collect(Collectors.toList());
    }

    /**
     * Adds a car to the repository.
     *
     * @param car the car to add
     * @return {@code true} if the car was added successfully, {@code false} otherwise
     */
    @Override
    public boolean add(Car car) {
        return carRepo.create(car);
    }

    /**
     * Deletes a car from the repository.
     *
     * @param car the car to delete
     * @return {@code true} if the car was deleted successfully, {@code false} otherwise
     */
    @Override
    public boolean delete(Car car) {
        return carRepo.delete(car);
    }

    /**
     * Retrieves a car that matches the specified filter.
     *
     * @param predicate the filter criteria
     * @return an {@link Optional} containing the first car that matches the filter, or an empty {@link Optional} if no car matches
     */
    @Override
    public Optional<Car> getCarByFilter(Predicate<Car> predicate) {
        return carRepo.findByFilter(predicate).findFirst();
    }

    /**
     * Retrieves a car by its ID.
     *
     * @param id the ID of the car
     * @return an {@link Optional} containing the car with the specified ID, or an empty {@link Optional} if no car matches
     */
    @Override
    public Optional<Car> getById(int id) {
        return carRepo.findByFilter(car -> car.getId() == id).findFirst();
    }

    /**
     * Retrieves all cars from the repository.
     *
     * @return a list of all cars
     */
    @Override
    public List<Car> getAllCar() {
        return carRepo.findAll();
    }

    /**
     * Retrieves all cars that match the specified filter.
     *
     * @param predicate the filter criteria
     * @return a list of cars that match the filter
     */
    @Override
    public List<Car> getCarsByFilter(Predicate<Car> predicate) {
        return carRepo.findByFilter(predicate).toList();
    }

    /**
     * Marks a car as "For Sale" if the user is not a client.
     *
     * @param car  the car to mark
     * @param user the user attempting to mark the car
     */
    @Override
    public void markForSale(Car car, User user) {
        if (user.getRole() == Role.CLIENT) return;
        car.setState(CarState.FOR_SALE);
    }

    /**
     * Marks a car as "Sold" if the user is not a client.
     *
     * @param car  the car to mark
     * @param user the user attempting to mark the car
     */
    @Override
    public void markForSold(Car car, User user) {
        if (user.getRole() == Role.CLIENT) return;
        car.setState(CarState.SOLD);
    }

    /**
     * Marks a car as "Not for Sale" if the user is not a client.
     *
     * @param car  the car to mark
     * @param user the user attempting to mark the car
     */
    @Override
    public void markForNotSale(Car car, User user) {
        if (user.getRole() == Role.CLIENT) return;
        car.setState(CarState.NOT_SALE);
    }

    /**
     * Marks a car as "For Service" if the user is not a client.
     *
     * @param car  the car to mark
     * @param user the user attempting to mark the car
     */
    @Override
    public void markForService(Car car, User user) {
        if (user.getRole() == Role.CLIENT) return;
        car.setState(CarState.FOR_SERVICE);
    }
}
