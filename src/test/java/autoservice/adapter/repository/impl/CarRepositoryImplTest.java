package autoservice.adapter.repository.impl;

import autoservice.adapter.config.DatabaseManager;
import autoservice.adapter.repository.CarRepository;
import autoservice.model.Car;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.Statement;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class CarRepositoryImplTest {


    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15.2")
                    .withDatabaseName("car_service")
                    .withUsername("test")
                    .withPassword("test");
    private static CarRepository carRepository;

    @BeforeAll
    public static void setUp() {
        DatabaseManager.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
        DatabaseManager.setUsername(postgreSQLContainer.getUsername());
        DatabaseManager.setPassword(postgreSQLContainer.getPassword());

        carRepository = new CarRepositoryImpl();

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            String createSchemaSQL = "CREATE SCHEMA IF NOT EXISTS car_service";
            statement.execute(createSchemaSQL);

            String createTableSQL = "CREATE TABLE IF NOT EXISTS car_service.car (" +
                    "id SERIAL PRIMARY KEY, " +
                    "make VARCHAR(255), " +
                    "model VARCHAR(255), " +
                    "year INT, " +
                    "price BIGINT, " +
                    "state VARCHAR(255)" +
                    ")";
            statement.execute(createTableSQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDown() {
        postgreSQLContainer.stop();
    }

    @Test
    public void testCreateCar() {
        var car = new Car("Toyota", "Camry", 2020, 30000);
        boolean isCreated = carRepository.create(car);
        assertTrue(isCreated, "Car should be created");

        var fetchedCarOpt = carRepository.findById(car.getId());
        assertTrue(fetchedCarOpt.isPresent(), "Car should be found");
        Assertions.assertEquals(car.getMake(), fetchedCarOpt.get().getMake(), "Make should match");
        Assertions.assertEquals(car.getModel(), fetchedCarOpt.get().getModel(), "Model should match");
    }
    @Test
    public void testDeleteCar() {
        var car = new Car("Toyota", "Camry", 2020, 30000);
        boolean isCreated = carRepository.create(car);
        assertTrue(isCreated, "Car should be created");

        boolean isDeleted = carRepository.delete(car);
        assertTrue(isDeleted, "Car should be deleted");

        var fetchedCarOpt = carRepository.findById(car.getId());
        assertFalse(fetchedCarOpt.isPresent(), "Car should not be found");
    }

    @Test
    public void testUpdateCar() {
        var car = new Car("Toyota", "Camry", 2020, 30000);
        boolean isCreated = carRepository.create(car);
        assertTrue(isCreated, "Car should be created");

        car.setPrice(32000);
        carRepository.update(car);

        var fetchedCarOpt = carRepository.findById(car.getId());
        assertTrue(fetchedCarOpt.isPresent(), "Car should be found");
        assertEquals(32000, fetchedCarOpt.get().getPrice(), "Price should match");
    }

    @Test
    public void testFindAllCars() {
        var car1 = new Car("Toyota", "Camry", 2020, 30000);
        var car2 = new Car("Honda", "Accord", 2021, 28000);
        carRepository.create(car1);
        carRepository.create(car2);

        var cars = carRepository.findAll();
        assertTrue(cars.size() >= 2, "There should be at least 2 cars");
        assertTrue(cars.contains(car1), "Car1 should be in the list");
        assertTrue(cars.contains(car2), "Car2 should be in the list");
    }
    @Test
    public void testFindByFilter() {
        var car1 = new Car( "Toyota", "Camry", 2020, 30000);
        var car2 = new Car( "Honda", "Accord", 2021, 28000);
        carRepository.create(car1);
        carRepository.create(car2);

        Predicate<Car> filter = car -> car.getPrice() > 29000;
        var filteredCars = carRepository.findByFilter(filter).toList();
        assertEquals(1, filteredCars.size(), "There should be 1 car matching the filter");
        assertTrue(filteredCars.contains(car1), "Car1 should be in the filtered list");
    }

}