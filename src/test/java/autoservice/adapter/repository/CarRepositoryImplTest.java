package autoservice.adapter.repository;

import autoservice.adapter.repository.impl.CarRepositoryImpl;
import autoservice.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CarRepositoryImplTest {

    private CarRepository carRepository;

    @BeforeEach
    void setUp() {
        carRepository = new CarRepositoryImpl();
    }

    @Test
    void testCreate() {
        Car car = new Car("Toyota", "Camry", 2020, 30000L);
        assertTrue(carRepository.create(car), "Car should be added successfully");
        assertTrue(carRepository.findAll().contains(car), "Car should be in the repository");
    }

    @Test
    void testDelete() {
        Car car = new Car("Toyota", "Camry", 2020, 30000L);
        carRepository.create(car);
        assertTrue(carRepository.delete(car), "Car should be removed successfully");
        assertFalse(carRepository.findAll().contains(car), "Car should not be in the repository");
    }

    @Test
    void testFindAll() {
        Car car1 = new Car("Toyota", "Camry", 2020, 30000L);
        Car car2 = new Car("Honda", "Civic", 2021, 25000L);
        carRepository.create(car1);
        carRepository.create(car2);

        List<Car> cars = carRepository.findAll();
        assertEquals(2, cars.size(), "There should be 2 cars in the repository");
        assertTrue(cars.contains(car1), "Car1 should be in the repository");
        assertTrue(cars.contains(car2), "Car2 should be in the repository");
    }

    @Test
    void testFindByFilter() {
        Car car1 = new Car("Toyota", "Camry", 2020, 30000L);
        Car car2 = new Car("Honda", "Civic", 2021, 25000L);
        carRepository.create(car1);
        carRepository.create(car2);

        Predicate<Car> isToyota = car -> "Toyota".equals(car.getMake());
        List<Car> toyotas = carRepository.findByFilter(isToyota).collect(Collectors.toList());

        assertEquals(1, toyotas.size(), "There should be 1 Toyota car in the repository");
        assertTrue(toyotas.contains(car1), "Car1 should be in the filtered results");
        assertFalse(toyotas.contains(car2), "Car2 should not be in the filtered results");
    }
}
