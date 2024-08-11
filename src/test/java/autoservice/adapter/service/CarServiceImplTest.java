package autoservice.adapter.service;

import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.service.impl.CarServiceImpl;
import autoservice.model.Car;
import autoservice.model.CarState;
import autoservice.model.Role;
import autoservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CarServiceImplTest {

    private CarRepository carRepo;
    private CarService carService;

    @BeforeEach
    void setUp() {
        carRepo = Mockito.mock(CarRepository.class);
        carService = new CarServiceImpl(carRepo);
    }

    @Test
    void testAddCar() {
        Car car = new Car("Toyota", "Corolla", 2020, 20000);
        when(carRepo.create(any(Car.class))).thenReturn(true);

        boolean result = carService.add(car);

        assertTrue(result, "Car should be added successfully");
    }

    @Test
    void testDeleteCar() {
        Car car = new Car("Toyota", "Corolla", 2020, 20000);
        when(carRepo.delete(any(Car.class))).thenReturn(true);

        boolean result = carService.delete(car);

        assertTrue(result, "Car should be deleted successfully");
    }

    @Test
    void testGetCarByFilter() {
        Car car = new Car("Toyota", "Corolla", 2020, 20000);
        when(carRepo.findByFilter(any(Predicate.class))).thenReturn(List.of(car).stream());

        Optional<Car> result = carService.getCarByFilter(c -> c.getMake().equals("Toyota"));

        assertTrue(result.isPresent(), "Car should be found by filter");
        assertEquals("Toyota", result.get().getMake(), "Car make should match the filter");
    }

    @Test
    void testGetById() {
        Car car = new Car("Toyota", "Corolla", 2020, 20000);
        when(carRepo.findByFilter(any(Predicate.class))).thenReturn(List.of(car).stream());

        Optional<Car> result = carService.getById(car.getId());

        assertTrue(result.isPresent(), "Car should be found by ID");
        assertEquals(car.getId(), result.get().getId(), "Car ID should match");
    }

    @Test
    void testGetAllCar() {
        Car car = new Car("Toyota", "Corolla", 2020, 20000);
        when(carRepo.findAll()).thenReturn(List.of(car));

        List<Car> result = carService.getAllCar();

        assertEquals(1, result.size(), "There should be 1 car in the list");
        assertEquals(car, result.get(0), "Car in the list should match the expected car");
    }

    @Test
    void testGetCarsByFilter() {
        Car car = new Car("Toyota", "Corolla", 2020, 20000);
        when(carRepo.findByFilter(any(Predicate.class))).thenReturn(List.of(car).stream());

        List<Car> result = carService.getCarsByFilter(c -> c.getMake().equals("Toyota"));

        assertEquals(1, result.size(), "There should be 1 car in the filtered list");
        assertEquals(car, result.get(0), "Car in the filtered list should match the expected car");
    }

    @Test
    void testMarkForSale() {
        Car car = new Car("Toyota", "Corolla", 2020, 20000);
        User user = new User(Role.MANAGER, "manager", "password");

        carService.changeStatus(car, user, CarState.FOR_SALE);

        assertEquals(CarState.FOR_SALE, car.getState(), "Car should be marked as FOR_SALE");
    }

    @Test
    void testMarkForSold() {
        Car car = new Car("Toyota", "Corolla", 2020, 20000);
        User user = new User(Role.MANAGER, "manager", "password");

        carService.changeStatus(car, user, CarState.SOLD);

        assertEquals(CarState.SOLD, car.getState(), "Car should be marked as SOLD");
    }

    @Test
    void testMarkForNotSale() {
        Car car = new Car("Toyota", "Corolla", 2020, 20000);
        User user = new User(Role.MANAGER, "manager", "password");

        carService.changeStatus(car, user, CarState.NOT_SALE);

        assertEquals(CarState.NOT_SALE, car.getState(), "Car should be marked as NOT_SALE");
    }

    @Test
    void testMarkForService() {
        Car car = new Car("Toyota", "Corolla", 2020, 20000);
        User user = new User(Role.MANAGER, "manager", "password");

        carService.changeStatus(car, user, CarState.FOR_SERVICE);

        assertEquals(CarState.FOR_SERVICE, car.getState(), "Car should be marked as FOR_SERVICE");
    }

    @Test
    void testFilterCarsByString() {
        Car car = new Car("Toyota", "Corolla", 2020, 20000);
        List<Car> cars = List.of(car);

        List<Car> result = CarServiceImpl.filterCarsByString(cars, "Toyota");

        assertEquals(1, result.size(), "There should be 1 car matching the filter");
        assertEquals(car, result.get(0), "Car should match the filter criteria");
    }
}
