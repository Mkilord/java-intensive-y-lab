package autoservice.adapter.repository;

import autoservice.adapter.config.DatabaseManager;
import autoservice.adapter.repository.impl.CarRepositoryImpl;
import autoservice.adapter.repository.impl.ServiceOrderRepositoryImpl;
import autoservice.adapter.repository.impl.UserRepositoryImpl;
import autoservice.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceOrderRepositoryImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15.2")
                    .withDatabaseName("car_service")
                    .withUsername("test")
                    .withPassword("test");

    private static ServiceOrderRepository serviceOrderRepository;
    private static CarRepository carRepository;
    private static UserRepository userRepository;

    @BeforeAll
    public static void setUpBeforeClass() {
        postgreSQLContainer.start();

        DatabaseManager.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
        DatabaseManager.setUsername(postgreSQLContainer.getUsername());
        DatabaseManager.setPassword(postgreSQLContainer.getPassword());

        carRepository = new CarRepositoryImpl();
        userRepository = new UserRepositoryImpl();
        serviceOrderRepository = new ServiceOrderRepositoryImpl(userRepository, carRepository);
    }

    @BeforeEach
    public void setUp() {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("DROP TABLE IF EXISTS car_service.service_order CASCADE");
            statement.execute("DROP TABLE IF EXISTS car_service.car CASCADE");
            statement.execute("DROP TABLE IF EXISTS car_service.user CASCADE");

            statement.execute("CREATE SCHEMA IF NOT EXISTS car_service");

            statement.execute("CREATE TABLE car_service.user (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username VARCHAR(255) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "name VARCHAR(255), " +
                    "surname VARCHAR(255), " +
                    "phone VARCHAR(255), " +
                    "role VARCHAR(255) NOT NULL" +
                    ")");
            statement.execute("CREATE TABLE car_service.car (" +
                    "id SERIAL PRIMARY KEY, " +
                    "make VARCHAR(255), " +
                    "model VARCHAR(255), " +
                    "year INT, " +
                    "price INT, " +
                    "state VARCHAR(255)" +
                    ")");
            statement.execute("CREATE TABLE car_service.service_order (" +
                    "id SERIAL PRIMARY KEY, " +
                    "customer_id INT, " +
                    "car_id INT, " +
                    "date DATE, " +
                    "status VARCHAR(255), " +
                    "FOREIGN KEY (customer_id) REFERENCES car_service.user(id), " +
                    "FOREIGN KEY (car_id) REFERENCES car_service.car(id)" +
                    ")");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @AfterEach
    public void tearDown() {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM car_service.service_order");
            statement.execute("DELETE FROM car_service.car");
            statement.execute("DELETE FROM car_service.user");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateServiceOrder() {
        var customer = new User(Role.CLIENT, "user" + System.currentTimeMillis(), "pass",
                "test", "test2", "34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        assertNotEquals(0, customer.getId(), "User ID should be generated");

        var car = new Car("Toyota", "Camry", 2020, 30000);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        var serviceOrder = new ServiceOrder(customer, car);
        boolean serviceOrderCreated = serviceOrderRepository.create(serviceOrder);
        assertTrue(serviceOrderCreated, "Service order should be created");

        var fetchedOrderOpt = serviceOrderRepository.findById(serviceOrder.getId());
        assertTrue(fetchedOrderOpt.isPresent(), "Service order should be found");
        assertEquals(customer.getId(), fetchedOrderOpt.get().getCustomer().getId(), "Customer ID should match");
        assertEquals(car.getId(), fetchedOrderOpt.get().getCar().getId(), "Car ID should match");
        assertEquals(serviceOrder.getStatus(), fetchedOrderOpt.get().getStatus(), "Order status should match");
        assertEquals(serviceOrder.getDate(), fetchedOrderOpt.get().getDate(), "Order date should match");
    }

    @Test
    public void testDeleteServiceOrder() {
        var customer = new User(Role.CLIENT, "user" + System.currentTimeMillis(), "pass",
                "test", "test2", "34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        var car = new Car("Toyota", "Camry", 2020, 30000);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        var serviceOrder = new ServiceOrder(customer, car);
        boolean serviceOrderCreated = serviceOrderRepository.create(serviceOrder);
        assertTrue(serviceOrderCreated, "Service order should be created");

        boolean serviceOrderDeleted = serviceOrderRepository.delete(serviceOrder);
        assertTrue(serviceOrderDeleted, "Service order should be deleted");

        var fetchedOrderOpt = serviceOrderRepository.findById(serviceOrder.getId());
        assertFalse(fetchedOrderOpt.isPresent(), "Service order should not be found after deletion");
    }

    @Test
    public void testUpdateServiceOrder() {
        var customer = new User(Role.CLIENT, "user" + System.currentTimeMillis(), "pass",
                "test", "test2", "34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        var car = new Car( "Toyota", "Camry", 2020, 30000);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        var serviceOrder = new ServiceOrder(customer, car);
        boolean serviceOrderCreated = serviceOrderRepository.create(serviceOrder);
        assertTrue(serviceOrderCreated, "Service order should be created");

        serviceOrder.setStatus(OrderStatus.COMPLETE);
        serviceOrderRepository.update(serviceOrder);

        var fetchedOrderOpt = serviceOrderRepository.findById(serviceOrder.getId());
        assertTrue(fetchedOrderOpt.isPresent(), "Service order should be found");
        assertEquals(OrderStatus.COMPLETE, fetchedOrderOpt.get().getStatus(), "Order status should match after update");
    }

    @Test
    public void testFindById() {
        var customer = new User(Role.CLIENT, "user" + System.currentTimeMillis(), "pass",
                "test", "test2", "34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        var car = new Car( "Toyota", "Camry", 2020, 30000);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        var serviceOrder = new ServiceOrder( customer, car);
        boolean serviceOrderCreated = serviceOrderRepository.create(serviceOrder);
        assertTrue(serviceOrderCreated, "Service order should be created");

        var fetchedOrderOpt = serviceOrderRepository.findById(serviceOrder.getId());
        assertTrue(fetchedOrderOpt.isPresent(), "Service order should be found");
        assertEquals(customer.getId(), fetchedOrderOpt.get().getCustomer().getId(), "Customer ID should match");
        assertEquals(car.getId(), fetchedOrderOpt.get().getCar().getId(), "Car ID should match");
        assertEquals(serviceOrder.getStatus(), fetchedOrderOpt.get().getStatus(), "Order status should match");
        assertEquals(serviceOrder.getDate(), fetchedOrderOpt.get().getDate(), "Order date should match");
    }

    @Test
    public void testFindAll() {
        var customer1 = new User(Role.CLIENT, "user" + System.currentTimeMillis(), "pass",
                "test", "test2", "34939483");

        var customer2 = new User(Role.CLIENT, "user2" + System.currentTimeMillis(), "pass",
                "test", "test2", "34939484");

        boolean user1Created = userRepository.create(customer1);
        boolean user2Created = userRepository.create(customer2);
        assertTrue(user1Created, "First user should be created");
        assertTrue(user2Created, "Second user should be created");

        var car1 = new Car( "Toyota", "Camry", 2020, 30000);

        var car2 = new Car( "Honda", "Civic", 2021, 25000);

        boolean car1Created = carRepository.create(car1);
        boolean car2Created = carRepository.create(car2);
        assertTrue(car1Created, "First car should be created");
        assertTrue(car2Created, "Second car should be created");

        var order1 = new ServiceOrder(customer1, car1);
        var order2 = new ServiceOrder(LocalDate.now().plusDays(1), OrderStatus.COMPLETE, customer2, car2);

        boolean order1Created = serviceOrderRepository.create(order1);
        boolean order2Created = serviceOrderRepository.create(order2);
        assertTrue(order1Created, "First order should be created");
        assertTrue(order2Created, "Second order should be created");

        var allOrders = serviceOrderRepository.findAll();
        assertEquals(2, allOrders.size(), "Should return all orders");
        assertTrue(allOrders.stream().anyMatch(o -> o.getId() == order1.getId()), "First order should be present");
        assertTrue(allOrders.stream().anyMatch(o -> o.getId() == order2.getId()), "Second order should be present");
    }

    @Test
    public void testFindByFilter() {
        var customer = new User(Role.CLIENT, "user" + System.currentTimeMillis(), "pass",
                "test", "test2", "34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        var car = new Car( "Toyota", "Camry", 2020, 30000);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        var order1 = new ServiceOrder(customer, car);
        var order2 = new ServiceOrder(LocalDate.now().plusDays(1), OrderStatus.COMPLETE, customer, car);

        boolean order1Created = serviceOrderRepository.create(order1);
        boolean order2Created = serviceOrderRepository.create(order2);
        assertTrue(order1Created, "First order should be created");
        assertTrue(order2Created, "Second order should be created");

        Predicate<ServiceOrder> completedOrdersFilter = o -> o.getStatus() == OrderStatus.COMPLETE;
        var completedOrders = serviceOrderRepository.findByFilter(completedOrdersFilter).toList();
        assertEquals(1, completedOrders.size(), "Should return only completed orders");
        assertEquals(order2.getId(), completedOrders.get(0).getId(), "Completed order should match");
    }
}
