package autoservice.adapter.service;

import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.repository.OrderRepository;
import autoservice.adapter.service.impl.SalesOrderServiceImpl;
import autoservice.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static autoservice.model.CarState.*;
import static autoservice.model.OrderStatus.*;
import static autoservice.model.OrderStatus.COMPLETE;
import static autoservice.model.OrderStatus.IN_PROGRESS;
import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.*;

class SalesOrderServiceImplTest {

    private OrderRepository orderRepo;
    private MyOrderService<SalesOrder> salesOrderService;

    @BeforeEach
    void setUp() {
        orderRepo = Mockito.mock(OrderRepository.class);
        salesOrderService = new SalesOrderServiceImpl(orderRepo, Mockito.mock(CarRepository.class));
    }

    @Test
    void testAddSuccess() {
        var car = new Car(FOR_SALE, "Toyota", "Corolla", 2022, 20000);

        var customer = new User(Role.CLIENT, "john_doe", "password");
        var salesOrder = new SalesOrder(now(), IN_PROGRESS, customer, car);

        Mockito.when(orderRepo.create(Mockito.any(SalesOrder.class))).thenReturn(true);

        boolean result = salesOrderService.add(salesOrder);

        assertTrue(result);
        assertEquals(SOLD, car.getState());
        Mockito.verify(orderRepo).create(salesOrder);
    }

    @Test
    void testAddCustomerNotClient() {
        var car = new Car(FOR_SALE, "Toyota", "Corolla", 2022, 20000);

        var customer = new User(Role.MANAGER, "jane_doe", "password");
        var salesOrder = new SalesOrder(now(), IN_PROGRESS, customer, car);

        var thrown = assertThrows(RuntimeException.class, () -> salesOrderService.add(salesOrder));

        assertEquals("Only the customer can add orders!", thrown.getMessage());
        Mockito.verify(orderRepo, Mockito.never()).create(Mockito.any(SalesOrder.class));
    }

    @Test
    void testAddCarNotForSale() {
        var car = new Car(SOLD, "Toyota", "Corolla", 2022, 20000);

        var customer = new User(Role.CLIENT, "john_doe", "password");
        var salesOrder = new SalesOrder(now(), IN_PROGRESS, customer, car);

        var thrown = assertThrows(RuntimeException.class, () -> salesOrderService.add(salesOrder));

        assertEquals("The order has not been created! The car has the status: " + SOLD, thrown.getMessage());
        Mockito.verify(orderRepo, Mockito.never()).create(Mockito.any(SalesOrder.class));
    }

    @Test
    void testFilterOrdersByString() {
        var car1 = new Car(FOR_SALE, "Honda", "Civic", 2022, 28000);
        User customer1 = new User(Role.CLIENT, "john_doe", "password",
                "test2", "test2", "89306703783");
        var order1 = new SalesOrder(now(), COMPLETE, customer1, car1);
        var car2 = new Car(FOR_SALE, "Ford", "Focus", 2020, 25000);
        var customer2 = new User(Role.MANAGER, "jane_doe", "password",
                "test3", "test4", "89306743783");
        var order2 = new SalesOrder(now(), IN_PROGRESS, customer2, car2);

        var orders = new ArrayList<Order>();
        orders.add(order1);
        orders.add(order2);

        var filteredOrders = SalesOrderServiceImpl.filterOrdersByString(orders, IN_PROGRESS.toString());
        assertEquals(1, filteredOrders.size());
        assertEquals(order2, filteredOrders.get(0));
    }

    @Test
    void testDelete() {
        var car = new Car(FOR_SALE, "Nissan", "Altima", 2023, 25000);
        var user = new User(Role.CLIENT, "john_doe", "password");
        var salesOrder = new SalesOrder(now(), IN_PROGRESS, user, car);
        Mockito.when(orderRepo.delete(Mockito.any(SalesOrder.class))).thenReturn(true);

        boolean result = salesOrderService.delete(salesOrder);

        assertTrue(result);
        Mockito.verify(orderRepo).delete(salesOrder);
    }

    @Test
    void testComplete() {
        var car = new Car(FOR_SALE, "Chevrolet", "Malibu", 2022, 22000);
        var user = new User(Role.CLIENT, "john_doe", "password");
        var salesOrder = new SalesOrder(now(), IN_PROGRESS, user, car);
        salesOrderService.complete(salesOrder);
        assertEquals(COMPLETE, salesOrder.getStatus());
    }

    @Test
    void testCancel() {
        var car = new Car(SOLD, "Subaru", "Impreza", 2021, 21000);
        var salesOrder = new SalesOrder(now(), IN_PROGRESS, new User(Role.CLIENT, "john_doe", "password"), car);
        salesOrderService.cancel(salesOrder);
        assertEquals(FOR_SALE, car.getState());
        assertEquals(CANCEL, salesOrder.getStatus());
    }

    @Test
    void testInProgress() {
        var car = new Car(FOR_SALE,"BMW", "3 Series", 2023, 30000);
        var user = new User(Role.CLIENT, "john_doe", "password");
        var salesOrder = new SalesOrder(now(),IN_PROGRESS,user, car);
        salesOrderService.inProgress(salesOrder);
        assertEquals(SOLD, car.getState());
        assertEquals(IN_PROGRESS, salesOrder.getStatus());
    }
}
