package autoservice.adapter.repository.impl;

import autoservice.adapter.config.DatabaseManager;
import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.repository.UserRepository;
import autoservice.adapter.repository.impl.CarRepositoryImpl;
import autoservice.adapter.repository.impl.OrderRepositoryImpl;
import autoservice.domen.model.*;
import autoservice.domen.model.enums.OrderStatus;
import autoservice.domen.model.enums.Role;
import autoservice.model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class OrderRepositoryImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15.2")
                    .withDatabaseName("car_service")
                    .withUsername("test")
                    .withPassword("test");

    private static OrderRepositoryImpl orderRepository;
    private static CarRepository carRepository;
    private static UserRepository userRepository;

    @BeforeAll
    public static void setUp() {
        postgreSQLContainer.start();

        DatabaseManager.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
        DatabaseManager.setUsername(postgreSQLContainer.getUsername());
        DatabaseManager.setPassword(postgreSQLContainer.getPassword());

        carRepository = new CarRepositoryImpl();
        userRepository = new UserRepositoryImpl();
        orderRepository = new OrderRepositoryImpl(userRepository, carRepository);

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("CREATE SCHEMA IF NOT EXISTS car_service");

            statement.execute("CREATE TABLE IF NOT EXISTS car_service.user (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username VARCHAR(255) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "name VARCHAR(255), " +
                    "surname VARCHAR(255), " +
                    "phone VARCHAR(255), " +
                    "role VARCHAR(255) NOT NULL" +
                    ")");
            statement.execute("CREATE TABLE IF NOT EXISTS car_service.car (" +
                    "id SERIAL PRIMARY KEY, " +
                    "make VARCHAR(255), " +
                    "model VARCHAR(255), " +
                    "year INT, " +
                    "price INT, " +
                    "state VARCHAR(255)" +
                    ")");
            statement.execute("CREATE TABLE IF NOT EXISTS car_service.sales_order (" +
                    "id SERIAL PRIMARY KEY, " +
                    "customer_id INT, " +
                    "car_id INT, " +
                    "date DATE, " +
                    "status VARCHAR(255)" +
                    ")");

            statement.execute("ALTER TABLE car_service.sales_order " +
                    "ADD CONSTRAINT fk_customer " +
                    "FOREIGN KEY (customer_id) REFERENCES car_service.user(id) ON DELETE CASCADE");

            statement.execute("ALTER TABLE car_service.sales_order " +
                    "ADD CONSTRAINT fk_car " +
                    "FOREIGN KEY (car_id) REFERENCES car_service.car(id) ON DELETE CASCADE");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM car_service.user");
            statement.execute("DELETE FROM car_service.car");
            statement.execute("DELETE FROM car_service.sales_order");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateOrder() {
        var customer = new User(Role.CLIENT, "user6", "pass",
                "test", "test2", "34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        assertNotEquals(0, customer.getId(), "User ID should be generated");

        var car = new Car("Toyota", "Camry", 2020, 30000);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        SalesOrder order = new SalesOrder(LocalDate.now(), OrderStatus.IN_PROGRESS, customer, car);
        boolean orderCreated = orderRepository.create(order);
        assertTrue(orderCreated, "Order should be created");

        var fetchedOrderOpt = orderRepository.findById(order.getId());
        assertTrue(fetchedOrderOpt.isPresent(), "Order should be found");
        assertEquals(customer.getId(), fetchedOrderOpt.get().getCustomer().getId(), "Customer ID should match");
        assertEquals(car.getId(), fetchedOrderOpt.get().getCar().getId(), "Car ID should match");
        assertEquals(order.getStatus(), fetchedOrderOpt.get().getStatus(), "Order status should match");
        assertEquals(order.getDate(), fetchedOrderOpt.get().getDate(), "Order date should match");
    }

    @Test
    public void testDeleteOrder() {
        User customer = new User(Role.CLIENT, "user2", "pass",
                "test", "test2", "34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        var car = new Car("Toyota", "Camry", 2020, 30000);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        var order = new SalesOrder(customer, car);
        boolean orderCreated = orderRepository.create(order);
        assertTrue(orderCreated, "Order should be created");

        boolean orderDeleted = orderRepository.delete(order);
        assertTrue(orderDeleted, "Order should be deleted");

        var fetchedOrderOpt = orderRepository.findById(order.getId());
        assertFalse(fetchedOrderOpt.isPresent(), "Order should not be found after deletion");
    }

    @Test
    public void testUpdateOrder() {
        User customer = new User(Role.CLIENT, "user3", "pass", "test", "test2", "34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        var car = new Car("Toyota", "Camry", 2020, 30000);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        var order = new SalesOrder(LocalDate.now().plusDays(1), OrderStatus.COMPLETE, customer, car);
        boolean orderCreated = orderRepository.create(order);
        assertTrue(orderCreated, "Order should be created");

        orderRepository.update(order);
        var fetchedOrderOpt = orderRepository.findById(order.getId());
        assertTrue(fetchedOrderOpt.isPresent(), "Order should be found");
        assertEquals(OrderStatus.COMPLETE, fetchedOrderOpt.get().getStatus(), "Order status should match after update");
        assertEquals(LocalDate.now().plusDays(1), fetchedOrderOpt.get().getDate(), "Order date should match after update");
    }

    @Test
    public void testFindById() {
        var customer = new User(Role.CLIENT, "user7", "pass",
                "test", "test2", "34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        var car = new Car("Toyota", "Camry", 2020, 30000);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        var order = new SalesOrder(customer, car);
        boolean orderCreated = orderRepository.create(order);
        assertTrue(orderCreated, "Order should be created");

        var fetchedOrderOpt = orderRepository.findById(order.getId());
        assertTrue(fetchedOrderOpt.isPresent(), "Order should be found");
        assertEquals(customer.getId(), fetchedOrderOpt.get().getCustomer().getId(), "Customer ID should match");
        assertEquals(car.getId(), fetchedOrderOpt.get().getCar().getId(), "Car ID should match");
    }

    @AfterAll
    public static void tearDownAll() {
        postgreSQLContainer.stop();
    }
}
