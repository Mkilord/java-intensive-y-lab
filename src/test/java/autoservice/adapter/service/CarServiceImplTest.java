package autoservice.adapter.service;

import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.service.impl.CarServiceImpl;
import autoservice.model.Car;
import autoservice.model.Role;
import autoservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static autoservice.model.CarState.*;
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
        var car = new Car(FOR_SALE,"Toyota", "Corolla", 2020, 20000);
        when(carRepo.create(any(Car.class))).thenReturn(true);

        boolean result = carService.add(car);

        assertTrue(result, "Car should be added successfully");
    }

    @Test
    void testDeleteCar() {
        var car = new Car(FOR_SALE,"Toyota", "Corolla", 2020, 20000);
        when(carRepo.delete(any(Car.class))).thenReturn(true);

        boolean result = carService.delete(car);

        assertTrue(result, "Car should be deleted successfully");
    }

    @Test
    void testGetCarByFilter() {
        var car = new Car(FOR_SALE,"Toyota", "Corolla", 2020, 20000);

        when(carRepo.findByFilter(any(Predicate.class))).thenReturn(Stream.of(car));

        var resultOpt = carService.getCarByFilter(c -> c.getMake().equals("Toyota"));
        assertTrue(resultOpt.isPresent(), "Car should be found by filter");
        assertEquals("Toyota", resultOpt.get().getMake(), "Car make should match the filter");
    }

    @Test
    void testGetById() {
        var car = new Car(0,FOR_SALE,"Toyota", "Corolla", 2020, 20000);

        when(carRepo.findById(car.getId())).thenReturn(Optional.of(car));

        var resultOpt = carService.getById(car.getId());
        assertTrue(resultOpt.isPresent(), "Car should be found by ID");
        assertEquals(car.getId(), resultOpt.get().getId(), "Car ID should match");
    }

    @Test
    void testGetAllCar() {
        var car = new Car(FOR_SALE,"Toyota", "Corolla", 2020, 20000);
        when(carRepo.findAll()).thenReturn(List.of(car));

        var resultOpt = carService.getAllCar();

        assertEquals(1, resultOpt.size(), "There should be 1 car in the list");
        assertEquals(car, resultOpt.get(0), "Car in the list should match the expected car");
    }

    @Test
    void testGetCarsByFilter() {
        var car = new Car(FOR_SALE,"Toyota", "Corolla", 2020, 20000);

        when(carRepo.findByFilter(any(Predicate.class))).thenReturn(Stream.of(car));

        var resultOpt = carService.getCarsByFilter(c -> c.getMake().equals("Toyota"));
        assertEquals(1, resultOpt.size(), "There should be 1 car in the filtered list");
        assertEquals(car, resultOpt.get(0), "Car in the filtered list should match the expected car");
    }

    @Test
    void testMarkForSale() {
        var car = new Car(FOR_SALE,"Toyota", "Corolla", 2020, 20000);
        var user = new User(Role.MANAGER, "manager", "password");

        carService.changeStatus(car, user, FOR_SALE);

        assertEquals(FOR_SALE, car.getState(), "Car should be marked as FOR_SALE");
    }

    @Test
    void testMarkForSold() {
        var car = new Car(FOR_SALE,"Toyota", "Corolla", 2020, 20000);
        var user = new User(Role.MANAGER, "manager", "password");

        carService.changeStatus(car, user, SOLD);

        assertEquals(SOLD, car.getState(), "Car should be marked as SOLD");
    }

    @Test
    void testMarkForNotSale() {
        var car = new Car(FOR_SALE,"Toyota", "Corolla", 2020, 20000);
        var user = new User(Role.MANAGER, "manager", "password");

        carService.changeStatus(car, user, NOT_SALE);

        assertEquals(NOT_SALE, car.getState(), "Car should be marked as NOT_SALE");
    }

    @Test
    void testMarkForService() {
        var car = new Car(FOR_SALE,"Toyota", "Corolla", 2020, 20000);
        var user = new User(Role.MANAGER, "manager", "password");

        carService.changeStatus(car, user, FOR_SERVICE);

        assertEquals(FOR_SERVICE, car.getState(), "Car should be marked as FOR_SERVICE");
    }

    @Test
    void testFilterCarsByString() {
        var car = new Car(FOR_SALE,"Toyota", "Corolla", 2020, 20000);
        var cars = List.of(car);

        var resultCars = CarServiceImpl.filterCarsByString(cars, "Toyota");

        assertEquals(1, resultCars.size(), "There should be 1 car matching the filter");
        assertEquals(car, resultCars.get(0), "Car should match the filter criteria");
    }
}
