package autoservice.adapter.service.impl;

import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.service.CarService;
import autoservice.adapter.service.RoleException;
import autoservice.model.Car;
import autoservice.model.CarState;
import autoservice.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public boolean add(Role role, Car car) throws RoleException {
        if (role != Role.ADMIN) throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        return carRepo.create(car);
    }

    /**
     * Deletes a car from the repository.
     *
     * @param car the car to delete
     * @return {@code true} if the car was deleted successfully, {@code false} otherwise
     */
    @Override
    public boolean delete(Role role, Car car) throws RoleException {
        if (role != Role.ADMIN) throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        return carRepo.delete(car);
    }

    /**
     * Retrieves a car that matches the specified filter.
     *
     * @param predicate the filter criteria
     * @return an {@link Optional} containing the first car that matches the filter, or an empty {@link Optional} if no car matches
     */
    @Override
    public Optional<Car> getCarByFilter(Role role, Predicate<Car> predicate) {
        if (role != Role.CLIENT) {
            return carRepo.findByFilter(((Predicate<Car>) car -> car.getState().equals(CarState.FOR_SALE))
                    .and(predicate)).findFirst();
        }
        return carRepo.findByFilter(predicate).findFirst();
    }

    /**
     * Retrieves a car by its ID.
     *
     * @param id the ID of the car
     * @return an {@link Optional} containing the car with the specified ID, or an empty {@link Optional} if no car matches
     */
    @Override
    public Optional<Car> getById(Role role, int id) {
        if (role == Role.CLIENT) {
            return carRepo.findByFilter(car -> car.getId() == id
                    && car.getState().equals(CarState.FOR_SALE)).findFirst();
        }
        return carRepo.findById(id);
    }

    /**
     * Retrieves all cars from the repository.
     *
     * @return a list of all cars
     */
    @Override
    public List<Car> getAllCar(Role role) {
        return getAllCarStream(role).toList();
    }

    @Override
    public Stream<Car> getAllCarStream(Role role) {
        if (role == Role.CLIENT) {
            return carRepo.findByFilter(car -> car.getState().equals(CarState.FOR_SALE));
        }
        return carRepo.findAll().stream();
    }

    @Override
    public Stream<Car> getCarsByFilterStream(Role role, Predicate<Car> predicate) {
        if (role != Role.CLIENT) {
            return carRepo.findByFilter(((Predicate<Car>) car -> car.getState().equals(CarState.FOR_SALE))
                    .and(predicate));
        }
        return carRepo.findByFilter(predicate);
    }

    /**
     * Retrieves all cars that match the specified filter.
     *
     * @param predicate the filter criteria
     * @return a list of cars that match the filter
     */

    @Override
    public List<Car> getCarsByFilter(Role role, Predicate<Car> predicate) {
        return getCarsByFilterStream(role, predicate).toList();
    }

    @Override
    public void editCar(Role role, Car car) throws RoleException {
        if (role != Role.ADMIN) throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        carRepo.update(car);
    }

    @Override
    public void changeStatus(Role role, Car car, CarState newState) throws RoleException {
        if (role != Role.ADMIN) throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        car.setState(newState);
        carRepo.update(car);
    }
}
