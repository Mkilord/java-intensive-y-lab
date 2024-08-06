package autoservice.adapter.service;

import autoservice.core.model.*;
import autoservice.core.port.OrderRepository;
import autoservice.core.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceImplTest {

    private OrderRepository orderRepo;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepo = Mockito.mock(OrderRepository.class);
        orderService = new OrderServiceImpl(orderRepo);
    }

    @Test
    void testAddSuccess() {
        Car car = new Car("Toyota", "Corolla", 2022, 20000);
        car.setState(CarState.FOR_SALE);

        User customer = new User(Role.CLIENT, "john_doe", "password");
        SalesOrder salesOrder = new SalesOrder(customer, car);

        Mockito.when(orderRepo.create(Mockito.any(SalesOrder.class))).thenReturn(true);

        boolean result = orderService.add(salesOrder);

        assertTrue(result);
        assertEquals(CarState.SOLD, car.getState());
        Mockito.verify(orderRepo).create(salesOrder);
    }

    @Test
    void testAddCustomerNotClient() {
        Car car = new Car("Toyota", "Corolla", 2022, 20000);
        car.setState(CarState.FOR_SALE);

        User customer = new User(Role.MANAGER, "jane_doe", "password");
        SalesOrder salesOrder = new SalesOrder(customer, car);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            orderService.add(salesOrder);
        });

        assertEquals("Only the customer can add orders!", thrown.getMessage());
        Mockito.verify(orderRepo, Mockito.never()).create(Mockito.any(SalesOrder.class));
    }

    @Test
    void testAddCarNotForSale() {
        Car car = new Car("Toyota", "Corolla", 2022, 20000);
        car.setState(CarState.SOLD);

        User customer = new User(Role.CLIENT, "john_doe", "password");
        SalesOrder salesOrder = new SalesOrder(customer, car);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> orderService.add(salesOrder));

        assertEquals("The order has not been created! The car has the status: "+CarState.SOLD, thrown.getMessage());
        Mockito.verify(orderRepo, Mockito.never()).create(Mockito.any(SalesOrder.class));
    }

    @Test
    void testFilterOrdersByString() {
        Car car1 = new Car("Honda", "Civic", 2022, 28000);
        User customer1 = new User(Role.CLIENT, "john_doe", "password");
        customer1.setName("test2");
        customer1.setSurname("test2");
        customer1.setPhone("89306703783");
        SalesOrder order1 = new SalesOrder(customer1, car1);
        order1.setStatus(OrderStatus.COMPLETE);

        Car car2 = new Car("Ford", "Focus", 2020, 25000);
        User customer2 = new User(Role.MANAGER, "jane_doe", "password");
        customer2.setName("test3");
        customer2.setSurname("test4");
        customer2.setPhone("89306743783");
        SalesOrder order2 = new SalesOrder(customer2, car2);
        order2.setStatus(OrderStatus.IN_PROCESS);


        List<SalesOrder> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);


        List<SalesOrder> filteredOrders = OrderServiceImpl.filterOrdersByString(orders, OrderStatus.IN_PROCESS.toString());
        assertEquals(1, filteredOrders.size());
        assertEquals(order2, filteredOrders.get(0));
    }

    @Test
    void testDelete() {
        Car car = new Car("Nissan", "Altima", 2023, 25000);
        SalesOrder salesOrder = new SalesOrder(new User(Role.CLIENT, "john_doe", "password"), car);
        Mockito.when(orderRepo.delete(Mockito.any(SalesOrder.class))).thenReturn(true);

        boolean result = orderService.delete(salesOrder);

        assertTrue(result);
        Mockito.verify(orderRepo).delete(salesOrder);
    }

    @Test
    void testComplete() {
        SalesOrder salesOrder = new SalesOrder(new User(Role.CLIENT, "john_doe", "password"), new Car("Chevrolet", "Malibu", 2022, 22000));
        orderService.complete(salesOrder);
        assertEquals(OrderStatus.COMPLETE, salesOrder.getStatus());
    }

    @Test
    void testCancel() {
        Car car = new Car("Subaru", "Impreza", 2021, 21000);
        car.setState(CarState.SOLD);
        SalesOrder salesOrder = new SalesOrder(new User(Role.CLIENT, "john_doe", "password"), car);
        orderService.cancel(salesOrder);
        assertEquals(CarState.FOR_SALE, car.getState());
        assertEquals(OrderStatus.CANCEL, salesOrder.getStatus());
    }

    @Test
    void testInProgress() {
        Car car = new Car("BMW", "3 Series", 2023, 30000);
        SalesOrder salesOrder = new SalesOrder(new User(Role.CLIENT, "john_doe", "password"), car);
        orderService.inProgress(salesOrder);
        assertEquals(CarState.SOLD, car.getState());
        assertEquals(OrderStatus.IN_PROCESS, salesOrder.getStatus());
    }
}
