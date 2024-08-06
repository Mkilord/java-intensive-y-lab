package autoservice.adapter.repository;

import autoservice.core.model.Car;
import autoservice.core.model.Role;
import autoservice.core.model.SalesOrder;
import autoservice.core.model.User;
import autoservice.core.port.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryImplTest {

    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderRepositoryImpl();
    }

    @Test
    void testCreate() {
        User customer = new User(Role.CLIENT, "johndoe", "password123");
        Car car = new Car("Toyota", "Camry", 2020, 30000L);
        SalesOrder order = new SalesOrder(customer, car);

        assertTrue(orderRepository.create(order), "SalesOrder should be added successfully");
        assertTrue(orderRepository.findAll().contains(order), "SalesOrder should be in the repository");
    }

    @Test
    void testDelete() {
        User customer = new User(Role.CLIENT, "johndoe", "password123");
        Car car = new Car("Toyota", "Camry", 2020, 30000L);
        SalesOrder order = new SalesOrder(customer, car);
        orderRepository.create(order);

        assertTrue(orderRepository.delete(order), "SalesOrder should be removed successfully");
        assertFalse(orderRepository.findAll().contains(order), "SalesOrder should not be in the repository");
    }

    @Test
    void testFindAll() {
        User customer1 = new User(Role.CLIENT, "johndoe", "password123");
        User customer2 = new User(Role.MANAGER, "janesmith", "password456");
        Car car1 = new Car("Toyota", "Camry", 2020, 30000L);
        Car car2 = new Car("Honda", "Civic", 2021, 25000L);
        SalesOrder order1 = new SalesOrder(customer1, car1);
        SalesOrder order2 = new SalesOrder(customer2, car2);

        orderRepository.create(order1);
        orderRepository.create(order2);

        List<SalesOrder> orders = orderRepository.findAll();
        assertEquals(2, orders.size(), "There should be 2 sales orders in the repository");
        assertTrue(orders.contains(order1), "Order1 should be in the repository");
        assertTrue(orders.contains(order2), "Order2 should be in the repository");
    }

    @Test
    void testFindByFilter() {
        User customer1 = new User(Role.CLIENT, "johndoe", "password123");
        User customer2 = new User(Role.MANAGER, "janesmith", "password456");
        Car car1 = new Car("Toyota", "Camry", 2020, 30000L);
        Car car2 = new Car("Honda", "Civic", 2021, 25000L);
        SalesOrder order1 = new SalesOrder(customer1, car1);
        SalesOrder order2 = new SalesOrder(customer2, car2);

        orderRepository.create(order1);
        orderRepository.create(order2);

        Predicate<SalesOrder> filterCondition = order -> "Toyota".equals(order.getCar().getMake());
        List<SalesOrder> filteredOrders = orderRepository.findByFilter(filterCondition).collect(Collectors.toList());

        assertEquals(1, filteredOrders.size(), "There should be 1 Toyota car order in the repository");
        assertTrue(filteredOrders.contains(order1), "Order1 should be in the filtered results");
        assertFalse(filteredOrders.contains(order2), "Order2 should not be in the filtered results");
    }
}
