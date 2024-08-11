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
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        User customer = new User(Role.CLIENT, "user" + System.currentTimeMillis(), "pass");
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

        ServiceOrder serviceOrder = new ServiceOrder(0, customer, car, OrderStatus.IN_PROGRESS, LocalDate.now());
        boolean serviceOrderCreated = serviceOrderRepository.create(serviceOrder);
        assertTrue(serviceOrderCreated, "Service order should be created");

        Optional<ServiceOrder> fetchedOrder = serviceOrderRepository.findById(serviceOrder.getId());
        assertTrue(fetchedOrder.isPresent(), "Service order should be found");
        assertEquals(customer.getId(), fetchedOrder.get().getCustomer().getId(), "Customer ID should match");
        assertEquals(car.getId(), fetchedOrder.get().getCar().getId(), "Car ID should match");
        assertEquals(serviceOrder.getStatus(), fetchedOrder.get().getStatus(), "Order status should match");
        assertEquals(serviceOrder.getDate(), fetchedOrder.get().getDate(), "Order date should match");
    }

    @Test
    public void testDeleteServiceOrder() {
        User customer = new User(Role.CLIENT, "user" + System.currentTimeMillis(), "pass");
        customer.setName("test");
        customer.setSurname("test2");
        customer.setPhone("34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        Car car = new Car("Toyota", "Camry", 2020, 30000);
        car.setState(CarState.FOR_SALE);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        ServiceOrder serviceOrder = new ServiceOrder(0, customer, car, OrderStatus.IN_PROGRESS, LocalDate.now());
        boolean serviceOrderCreated = serviceOrderRepository.create(serviceOrder);
        assertTrue(serviceOrderCreated, "Service order should be created");

        boolean serviceOrderDeleted = serviceOrderRepository.delete(serviceOrder);
        assertTrue(serviceOrderDeleted, "Service order should be deleted");

        Optional<ServiceOrder> fetchedOrder = serviceOrderRepository.findById(serviceOrder.getId());
        assertFalse(fetchedOrder.isPresent(), "Service order should not be found after deletion");
    }

    @Test
    public void testUpdateServiceOrder() {
        User customer = new User(Role.CLIENT, "user" + System.currentTimeMillis(), "pass");
        customer.setName("test");
        customer.setSurname("test2");
        customer.setPhone("34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        Car car = new Car("Toyota", "Camry", 2020, 30000);
        car.setState(CarState.FOR_SALE);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        ServiceOrder serviceOrder = new ServiceOrder(0, customer, car, OrderStatus.IN_PROGRESS, LocalDate.now());
        boolean serviceOrderCreated = serviceOrderRepository.create(serviceOrder);
        assertTrue(serviceOrderCreated, "Service order should be created");

        serviceOrder.setStatus(OrderStatus.COMPLETE);
        serviceOrder.setDate(LocalDate.now().plusDays(1));
        serviceOrderRepository.update(serviceOrder);

        Optional<ServiceOrder> fetchedOrder = serviceOrderRepository.findById(serviceOrder.getId());
        assertTrue(fetchedOrder.isPresent(), "Service order should be found");
        assertEquals(OrderStatus.COMPLETE, fetchedOrder.get().getStatus(), "Order status should match after update");
        assertEquals(LocalDate.now().plusDays(1), fetchedOrder.get().getDate(), "Order date should match after update");
    }

    @Test
    public void testFindById() {
        User customer = new User(Role.CLIENT, "user" + System.currentTimeMillis(), "pass");
        customer.setName("test");
        customer.setSurname("test2");
        customer.setPhone("34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        Car car = new Car("Toyota", "Camry", 2020, 30000);
        car.setState(CarState.FOR_SALE);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        ServiceOrder serviceOrder = new ServiceOrder(0, customer, car, OrderStatus.IN_PROGRESS, LocalDate.now());
        boolean serviceOrderCreated = serviceOrderRepository.create(serviceOrder);
        assertTrue(serviceOrderCreated, "Service order should be created");

        Optional<ServiceOrder> fetchedOrder = serviceOrderRepository.findById(serviceOrder.getId());
        assertTrue(fetchedOrder.isPresent(), "Service order should be found");
        assertEquals(customer.getId(), fetchedOrder.get().getCustomer().getId(), "Customer ID should match");
        assertEquals(car.getId(), fetchedOrder.get().getCar().getId(), "Car ID should match");
        assertEquals(serviceOrder.getStatus(), fetchedOrder.get().getStatus(), "Order status should match");
        assertEquals(serviceOrder.getDate(), fetchedOrder.get().getDate(), "Order date should match");
    }

    @Test
    public void testFindAll() {
        User customer1 = new User(Role.CLIENT, "user" + System.currentTimeMillis(), "pass");
        customer1.setName("test");
        customer1.setSurname("test2");
        customer1.setPhone("34939483");

        User customer2 = new User(Role.CLIENT, "user2" + System.currentTimeMillis(), "pass");
        customer2.setName("test");
        customer2.setSurname("test2");
        customer2.setPhone("34939484");

        boolean user1Created = userRepository.create(customer1);
        boolean user2Created = userRepository.create(customer2);
        assertTrue(user1Created, "First user should be created");
        assertTrue(user2Created, "Second user should be created");

        Car car1 = new Car("Toyota", "Camry", 2020, 30000);
        car1.setState(CarState.FOR_SALE);

        Car car2 = new Car("Honda", "Civic", 2021, 25000);
        car2.setState(CarState.FOR_SALE);

        boolean car1Created = carRepository.create(car1);
        boolean car2Created = carRepository.create(car2);
        assertTrue(car1Created, "First car should be created");
        assertTrue(car2Created, "Second car should be created");

        ServiceOrder order1 = new ServiceOrder(0, customer1, car1, OrderStatus.IN_PROGRESS, LocalDate.now());
        ServiceOrder order2 = new ServiceOrder(0, customer2, car2, OrderStatus.COMPLETE, LocalDate.now().plusDays(1));

        boolean order1Created = serviceOrderRepository.create(order1);
        boolean order2Created = serviceOrderRepository.create(order2);
        assertTrue(order1Created, "First order should be created");
        assertTrue(order2Created, "Second order should be created");

        List<ServiceOrder> allOrders = serviceOrderRepository.findAll();
        assertEquals(2, allOrders.size(), "Should return all orders");
        assertTrue(allOrders.stream().anyMatch(o -> o.getId() == order1.getId()), "First order should be present");
        assertTrue(allOrders.stream().anyMatch(o -> o.getId() == order2.getId()), "Second order should be present");
    }

    @Test
    public void testFindByFilter() {
        User customer = new User(Role.CLIENT, "user" + System.currentTimeMillis(), "pass");
        customer.setName("test");
        customer.setSurname("test2");
        customer.setPhone("34939483");

        boolean userCreated = userRepository.create(customer);
        assertTrue(userCreated, "User should be created");

        Car car = new Car("Toyota", "Camry", 2020, 30000);
        car.setState(CarState.FOR_SALE);

        boolean carCreated = carRepository.create(car);
        assertTrue(carCreated, "Car should be created");

        ServiceOrder order1 = new ServiceOrder(0, customer, car, OrderStatus.IN_PROGRESS, LocalDate.now());
        ServiceOrder order2 = new ServiceOrder(0, customer, car, OrderStatus.COMPLETE, LocalDate.now().plusDays(1));

        boolean order1Created = serviceOrderRepository.create(order1);
        boolean order2Created = serviceOrderRepository.create(order2);
        assertTrue(order1Created, "First order should be created");
        assertTrue(order2Created, "Second order should be created");

        Predicate<ServiceOrder> completedOrdersFilter = o -> o.getStatus() == OrderStatus.COMPLETE;
        List<ServiceOrder> completedOrders = serviceOrderRepository.findByFilter(completedOrdersFilter).collect(Collectors.toList());
        assertEquals(1, completedOrders.size(), "Should return only completed orders");
        assertEquals(order2.getId(), completedOrders.get(0).getId(), "Completed order should match");
    }
}
