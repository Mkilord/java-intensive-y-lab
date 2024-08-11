package autoservice.adapter.repository;

import autoservice.adapter.config.DatabaseManager;
import autoservice.adapter.repository.impl.CarRepositoryImpl;
import autoservice.adapter.repository.impl.OrderRepositoryImpl;
import autoservice.adapter.repository.impl.UserRepositoryImpl;
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
        User customer = new User(Role.CLIENT, "user6", "pass");
        customer.setName("test");
        customer.setSurname("test2");
        customer.setPhone("34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        assertNotEquals(0, customer.getId(), "User ID should be generated");

        Car car = new Car("Toyota", "Camry", 2020, 30000);
        car.setState(CarState.FOR_SALE);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        SalesOrder order = new SalesOrder(0, customer, car, OrderStatus.IN_PROGRESS, LocalDate.now());
        boolean orderCreated = orderRepository.create(order);
        assertTrue(orderCreated, "Order should be created");

        var fetchedOrder = orderRepository.findById(order.getId());
        assertTrue(fetchedOrder.isPresent(), "Order should be found");
        assertEquals(customer.getId(), fetchedOrder.get().getCustomer().getId(), "Customer ID should match");
        assertEquals(car.getId(), fetchedOrder.get().getCar().getId(), "Car ID should match");
        assertEquals(order.getStatus(), fetchedOrder.get().getStatus(), "Order status should match");
        assertEquals(order.getDate(), fetchedOrder.get().getDate(), "Order date should match");
    }

    @Test
    public void testDeleteOrder() {
        User customer = new User(Role.CLIENT, "user2", "pass");
        customer.setName("test");
        customer.setSurname("test2");
        customer.setPhone("34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        Car car = new Car("Toyota", "Camry", 2020, 30000);
        car.setState(CarState.FOR_SALE);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        SalesOrder order = new SalesOrder(0, customer, car, OrderStatus.IN_PROGRESS, LocalDate.now());
        boolean orderCreated = orderRepository.create(order);
        assertTrue(orderCreated, "Order should be created");

        boolean orderDeleted = orderRepository.delete(order);
        assertTrue(orderDeleted, "Order should be deleted");

        var fetchedOrder = orderRepository.findById(order.getId());
        assertFalse(fetchedOrder.isPresent(), "Order should not be found after deletion");
    }

    @Test
    public void testUpdateOrder() {
        User customer = new User(Role.CLIENT, "user3", "pass");
        customer.setName("test");
        customer.setSurname("test2");
        customer.setPhone("34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        Car car = new Car("Toyota", "Camry", 2020, 30000);
        car.setState(CarState.FOR_SALE);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        SalesOrder order = new SalesOrder(0, customer, car, OrderStatus.IN_PROGRESS, LocalDate.now());
        boolean orderCreated = orderRepository.create(order);
        assertTrue(orderCreated, "Order should be created");

        order.setStatus(OrderStatus.COMPLETE);
        order.setDate(LocalDate.now().plusDays(1));
        orderRepository.update(order);

        var fetchedOrder = orderRepository.findById(order.getId());
        assertTrue(fetchedOrder.isPresent(), "Order should be found");
        assertEquals(OrderStatus.COMPLETE, fetchedOrder.get().getStatus(), "Order status should match after update");
        assertEquals(LocalDate.now().plusDays(1), fetchedOrder.get().getDate(), "Order date should match after update");
    }

    @Test
    public void testFindById() {
        User customer = new User(Role.CLIENT, "user7", "pass");
        customer.setName("test");
        customer.setSurname("test2");
        customer.setPhone("34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        Car car = new Car("Toyota", "Camry", 2020, 30000);
        car.setState(CarState.FOR_SALE);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        SalesOrder order = new SalesOrder(0, customer, car, OrderStatus.IN_PROGRESS, LocalDate.now());
        boolean orderCreated = orderRepository.create(order);
        assertTrue(orderCreated, "Order should be created");

        var fetchedOrder = orderRepository.findById(order.getId());
        assertTrue(fetchedOrder.isPresent(), "Order should be found");
        assertEquals(customer.getId(), fetchedOrder.get().getCustomer().getId(), "Customer ID should match");
        assertEquals(car.getId(), fetchedOrder.get().getCar().getId(), "Car ID should match");
    }

    @AfterAll
    public static void tearDownAll() {
        postgreSQLContainer.stop();
    }
}
