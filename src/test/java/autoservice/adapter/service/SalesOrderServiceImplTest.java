package autoservice.adapter.service;

import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.repository.OrderRepository;
import autoservice.adapter.service.impl.SalesOrderServiceImpl;
import autoservice.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static autoservice.model.CarState.*;
import static autoservice.model.OrderStatus.*;
import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SalesOrderServiceImplTest {

    private OrderRepository orderRepo;
    private CarRepository carRepo;
    private SalesOrderServiceImpl salesOrderService;

    @BeforeEach
    void setUp() {
        orderRepo = Mockito.mock(OrderRepository.class);
        carRepo = Mockito.mock(CarRepository.class);
        salesOrderService = new SalesOrderServiceImpl(orderRepo, carRepo);
    }

    @Test
    void testAddSuccess() throws RoleException, OrderException {
        var car = new Car(FOR_SALE, "Toyota", "Corolla", 2022, 20000);
        var customer = new User(Role.CLIENT, "john_doe", "password");
        var salesOrder = new SalesOrder(now(), IN_PROGRESS, customer, car);

        when(orderRepo.create(any(SalesOrder.class))).thenReturn(true);

        boolean result = salesOrderService.add(Role.CLIENT, salesOrder);

        assertTrue(result);
        assertEquals(SOLD, car.getState());
        verify(orderRepo).create(salesOrder);
        verify(carRepo).update(car);
    }

    @Test
    void testAddCustomerNotClient() {
        var car = new Car(FOR_SALE, "Toyota", "Corolla", 2022, 20000);
        var customer = new User(Role.MANAGER, "jane_doe", "password");
        var salesOrder = new SalesOrder(now(), IN_PROGRESS, customer, car);

        var thrown = assertThrows(RoleException.class, () -> salesOrderService.add(Role.MANAGER, salesOrder));

        assertEquals(RoleException.PERMISSION_ERROR_MSG, thrown.getMessage());
        verify(orderRepo, never()).create(any(SalesOrder.class));
    }

    @Test
    void testAddCarNotForSale() {
        var car = new Car(SOLD, "Toyota", "Corolla", 2022, 20000);
        var customer = new User(Role.CLIENT, "john_doe", "password");
        var salesOrder = new SalesOrder(now(), IN_PROGRESS, customer, car);

        var thrown = assertThrows(OrderException.class, () -> salesOrderService.add(Role.CLIENT, salesOrder));

        assertEquals(OrderException.INVALID_CAR_ERROR_MSG, thrown.getMessage());
        verify(orderRepo, never()).create(any(SalesOrder.class));
    }


    @Test
    void testDelete() throws RoleException {
        var car = new Car(FOR_SALE, "Nissan", "Altima", 2023, 25000);
        var user = new User(Role.ADMIN, "john_doe", "password");
        var salesOrder = new SalesOrder(now(), IN_PROGRESS, user, car);
        when(orderRepo.delete(any(SalesOrder.class))).thenReturn(true);

        boolean result = salesOrderService.delete(Role.ADMIN, salesOrder);

        assertTrue(result);
        verify(orderRepo).delete(salesOrder);
    }

    @Test
    void testComplete() throws RoleException {
        var car = new Car(FOR_SALE, "Chevrolet", "Malibu", 2022, 22000);
        var user = new User(Role.MANAGER, "john_doe", "password");
        var salesOrder = new SalesOrder(now(), IN_PROGRESS, user, car);
        salesOrderService.complete(Role.MANAGER, salesOrder);
        assertEquals(COMPLETE, salesOrder.getStatus());
        verify(orderRepo).update(salesOrder);
    }

    @Test
    void testCancel() {
        var car = new Car(SOLD, "Subaru", "Impreza", 2021, 21000);
        var salesOrder = new SalesOrder(now(), IN_PROGRESS, new User(Role.CLIENT, "john_doe", "password"), car);
        salesOrderService.cancel(salesOrder);
        assertEquals(FOR_SALE, car.getState());
        assertEquals(CANCEL, salesOrder.getStatus());
        verify(carRepo).update(car);
        verify(orderRepo).update(salesOrder);
    }

    @Test
    void testInProgress() throws RoleException {
        var car = new Car(FOR_SALE, "BMW", "3 Series", 2023, 30000);
        var user = new User(Role.MANAGER, "john_doe", "password");
        var salesOrder = new SalesOrder(now(), IN_PROGRESS, user, car);
        salesOrderService.inProgress(Role.MANAGER, salesOrder);
        assertEquals(SOLD, car.getState());
        assertEquals(IN_PROGRESS, salesOrder.getStatus());
        verify(carRepo).update(car);
        verify(orderRepo).update(salesOrder);
    }
}
