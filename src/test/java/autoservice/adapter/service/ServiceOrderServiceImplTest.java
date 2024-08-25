package autoservice.adapter.service;

import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.repository.ServiceOrderRepository;
import autoservice.adapter.service.impl.ServiceOrderServiceImpl;
import autoservice.domen.model.*;
import autoservice.domen.model.enums.CarState;
import autoservice.domen.model.enums.OrderStatus;
import autoservice.domen.model.enums.Role;
import autoservice.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.function.Predicate;

import static autoservice.domen.model.enums.CarState.FOR_SALE;
import static autoservice.domen.model.enums.CarState.SOLD;
import static org.junit.jupiter.api.Assertions.*;

class ServiceOrderServiceImplTest {

    private ServiceOrderRepository serviceOrderRepo;
    private CarRepository carRepo;
    private MyOrderService<ServiceOrder> serviceOrderService;

    @BeforeEach
    void setUp() {
        serviceOrderRepo = Mockito.mock(ServiceOrderRepository.class);
        carRepo = Mockito.mock(CarRepository.class);
        serviceOrderService = new ServiceOrderServiceImpl(serviceOrderRepo, carRepo);
    }

    @Test
    void testAdd() throws RoleException, OrderException {
        var car = new Car(FOR_SALE, "Toyota", "Camry", 2020, 30000L);
        var order = new ServiceOrder(new User(Role.CLIENT, "john_doe", "password"), car);

        Mockito.when(serviceOrderRepo.create(Mockito.any(ServiceOrder.class))).thenReturn(true);

        boolean result = serviceOrderService.add(Role.CLIENT, order);

        assertTrue(result);
        assertEquals(SOLD, car.getState());
        Mockito.verify(serviceOrderRepo).create(order);
        Mockito.verify(carRepo).update(car);
    }

    @Test
    void testAddRoleException() {
        var car = new Car(FOR_SALE, "Toyota", "Camry", 2020, 30000L);
        var order = new ServiceOrder(new User(Role.CLIENT, "john_doe", "password"), car);

        var thrown = assertThrows(RoleException.class, () -> serviceOrderService.add(Role.MANAGER, order));
        assertEquals(RoleException.PERMISSION_ERROR_MSG, thrown.getMessage());
        Mockito.verify(serviceOrderRepo, Mockito.never()).create(Mockito.any(ServiceOrder.class));
        Mockito.verify(carRepo, Mockito.never()).update(Mockito.any(Car.class));
    }

    @Test
    void testAddOrderException() {
        var car = new Car(SOLD, "Toyota", "Camry", 2020, 30000L);
        var order = new ServiceOrder(new User(Role.CLIENT, "john_doe", "password"), car);

        var thrown = assertThrows(OrderException.class, () -> serviceOrderService.add(Role.CLIENT, order));
        assertEquals(OrderException.INVALID_CAR_ERROR_MSG, thrown.getMessage());
        Mockito.verify(serviceOrderRepo, Mockito.never()).create(Mockito.any(ServiceOrder.class));
        Mockito.verify(carRepo, Mockito.never()).update(Mockito.any(Car.class));
    }

    @Test
    void testDelete() throws RoleException {
        var order = new ServiceOrder(new User(Role.CLIENT, "john_doe", "password"), new Car("Toyota", "Camry", 2020, 30000L));

        Mockito.when(serviceOrderRepo.delete(Mockito.any(ServiceOrder.class))).thenReturn(true);

        boolean result = serviceOrderService.delete(Role.ADMIN, order);

        assertTrue(result);
        Mockito.verify(serviceOrderRepo).delete(order);
    }

    @Test
    void testDeleteRoleException() {
        var order = new ServiceOrder(new User(Role.CLIENT, "john_doe", "password"), new Car("Toyota", "Camry", 2020, 30000L));

        var thrown = assertThrows(RoleException.class, () -> serviceOrderService.delete(Role.CLIENT, order));
        assertEquals(RoleException.PERMISSION_ERROR_MSG, thrown.getMessage());
        Mockito.verify(serviceOrderRepo, Mockito.never()).delete(Mockito.any(ServiceOrder.class));
    }

    @Test
    void testComplete() throws RoleException {
        var order = new ServiceOrder(new User(Role.MANAGER, "john_doe", "password"), new Car("Toyota", "Camry", 2020, 30000L));

        serviceOrderService.complete(Role.MANAGER, order);

        assertEquals(OrderStatus.COMPLETE, order.getStatus());
        Mockito.verify(serviceOrderRepo).update(order);
    }

    @Test
    void testCompleteRoleException() {
        var order = new ServiceOrder(new User(Role.CLIENT, "john_doe", "password"), new Car("Toyota", "Camry", 2020, 30000L));

        var thrown = assertThrows(RoleException.class, () -> serviceOrderService.complete(Role.CLIENT, order));
        assertEquals(RoleException.PERMISSION_ERROR_MSG, thrown.getMessage());
        Mockito.verify(serviceOrderRepo, Mockito.never()).update(Mockito.any(ServiceOrder.class));
    }

    @Test
    void testCancel() {
        var car = new Car(SOLD, "Toyota", "Camry", 2020, 30000L);
        var order = new ServiceOrder(new User(Role.CLIENT, "john_doe", "password"), car);

        serviceOrderService.cancel(order);

        assertEquals(OrderStatus.CANCEL, order.getStatus());
        assertEquals(CarState.NOT_SALE, car.getState());
        Mockito.verify(serviceOrderRepo).update(order);
        Mockito.verify(carRepo).update(car);
    }

    @Test
    void testInProgress() throws RoleException {
        var car = new Car(FOR_SALE, "Toyota", "Camry", 2020, 30000L);
        var order = new ServiceOrder(new User(Role.MANAGER, "john_doe", "password"), car);

        serviceOrderService.inProgress(Role.MANAGER, order);

        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
        assertEquals(SOLD, car.getState());
        Mockito.verify(serviceOrderRepo).update(order);
        Mockito.verify(carRepo).update(car);
    }

    @Test
    void testInProgressRoleException() {
        var car = new Car(FOR_SALE, "Toyota", "Camry", 2020, 30000L);
        var order = new ServiceOrder(new User(Role.CLIENT, "john_doe", "password"), car);

        var thrown = assertThrows(RoleException.class, () -> serviceOrderService.inProgress(Role.CLIENT, order));
        assertEquals(RoleException.PERMISSION_ERROR_MSG, thrown.getMessage());
        Mockito.verify(serviceOrderRepo, Mockito.never()).update(Mockito.any(ServiceOrder.class));
        Mockito.verify(carRepo, Mockito.never()).update(Mockito.any(Car.class));
    }

    @Test
    void testGetOrderByFilter() {
        var user = new User(Role.CLIENT, "john_doe", "password");
        var order = new ServiceOrder(user, new Car("Toyota", "Camry", 2020, 30000L));

        Mockito.when(serviceOrderRepo.findByFilter(Mockito.any(Predicate.class)))
                .thenReturn(List.of(order).stream());

        var result = serviceOrderService.getOrderByFilter(user, o -> o.getCustomer().getName().equals("john_doe"));

        assertTrue(result.isPresent());
        assertEquals(order, result.get());
        Mockito.verify(serviceOrderRepo).findByFilter(Mockito.any(Predicate.class));
    }
}
