package autoservice.adapter.service;

import autoservice.adapter.repository.ServiceOrderRepository;
import autoservice.adapter.service.impl.ServiceOrderServiceImpl;
import autoservice.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceSalesOrderServiceImplTest {

    private ServiceOrderRepository serviceOrderRepo;
    private ServiceOrderService serviceOrderService;

    @BeforeEach
    void setUp() {
        serviceOrderRepo = Mockito.mock(ServiceOrderRepository.class);
        serviceOrderService = new ServiceOrderServiceImpl(serviceOrderRepo);
    }

    @Test
    void testAdd() {
        ServiceOrder order = new ServiceOrder(
                new User(Role.CLIENT, "john_doe", "password"),
                new Car("Toyota", "Camry", 2020, 30000L)
        );
        Mockito.when(serviceOrderRepo.create(Mockito.any(ServiceOrder.class))).thenReturn(true);

        boolean result = serviceOrderService.add(order);

        assertTrue(result);
        Mockito.verify(serviceOrderRepo).create(order);
    }

    @Test
    void testDelete() {
        ServiceOrder order = new ServiceOrder(
                new User(Role.CLIENT, "john_doe", "password"),
                new Car("Toyota", "Camry", 2020, 30000L)
        );
        Mockito.when(serviceOrderRepo.delete(Mockito.any(ServiceOrder.class))).thenReturn(true);

        boolean result = serviceOrderService.delete(order);

        assertTrue(result);
        Mockito.verify(serviceOrderRepo).delete(order);
    }

    @Test
    void testComplete() {
        ServiceOrder order = new ServiceOrder(
                new User(Role.CLIENT, "john_doe", "password"),
                new Car("Toyota", "Camry", 2020, 30000L)
        );
        serviceOrderService.complete(order);
        assertEquals(OrderStatus.COMPLETE, order.getStatus());
    }

    @Test
    void testCancel() {
        ServiceOrder order = new ServiceOrder(
                new User(Role.CLIENT, "john_doe", "password"),
                new Car("Toyota", "Camry", 2020, 30000L)
        );
        serviceOrderService.cancel(order);
        assertEquals(OrderStatus.CANCEL, order.getStatus());
    }

    @Test
    void testInProgress() {
        ServiceOrder order = new ServiceOrder(
                new User(Role.CLIENT, "john_doe", "password"),
                new Car("Toyota", "Camry", 2020, 30000L)
        );
        serviceOrderService.inProgress(order);
        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
    }

    @Test
    void testGetAllSOrders() {
        ServiceOrder order1 = new ServiceOrder(
                new User(Role.CLIENT, "john_doe", "password"),
                new Car("Toyota", "Camry", 2020, 30000L)
        );
        ServiceOrder order2 = new ServiceOrder(
                new User(Role.CLIENT, "jane_doe", "password"),
                new Car("Honda", "Accord", 2021, 28000L)
        );
        List<ServiceOrder> orders = List.of(order1, order2);

        Mockito.when(serviceOrderRepo.findAll()).thenReturn(orders);

        List<ServiceOrder> result = serviceOrderService.getAllOrders();

        assertEquals(orders, result);
        Mockito.verify(serviceOrderRepo).findAll();
    }

    @Test
    void testGetSOrderByFilter() {
        ServiceOrder order = new ServiceOrder(
                new User(Role.CLIENT, "john_doe", "password"),
                new Car("Toyota", "Camry", 2020, 30000L)
        );
        Mockito.when(serviceOrderRepo.findByFilter(Mockito.any(Predicate.class)))
                .thenReturn(List.of(order).stream());

        Optional<ServiceOrder> result = serviceOrderService.getOrderByFilter(o -> o.getCustomer().getName().equals("john_doe"));

        assertTrue(result.isPresent());
        assertEquals(order, result.get());
        Mockito.verify(serviceOrderRepo).findByFilter(Mockito.any(Predicate.class));
    }

    @Test
    void testFilterOrdersByString() {
        var user1 = new User(Role.CLIENT, "john_doe", "password");
        user1.setName("test");
        user1.setSurname("test1");
        user1.setPhone("89106790581");
        ServiceOrder order1 = new ServiceOrder(
                user1,
                new Car("Toyota", "Camry", 2020, 30000L)
        );
        var user2 = new User(Role.CLIENT, "jane_doe", "password");
        user2.setName("test2");
        user2.setSurname("test2");
        user2.setPhone("89106790581");
        order1.setStatus(OrderStatus.COMPLETE);
        ServiceOrder order2 = new ServiceOrder(
                user2,
                new Car("Honda", "Accord", 2021, 28000L)
        );
        order2.setStatus(OrderStatus.IN_PROGRESS);
        List<ServiceOrder> orders = List.of(order1, order2);

        List<ServiceOrder> filteredOrders = ServiceOrderServiceImpl.filterOrdersByString(orders, "test");
        assertEquals(1, filteredOrders.size());
        assertEquals(order1, filteredOrders.get(0));

        filteredOrders = ServiceOrderServiceImpl.filterOrdersByString(orders, OrderStatus.IN_PROGRESS.toString());
        assertEquals(1, filteredOrders.size());
        assertEquals(order2, filteredOrders.get(0));

        filteredOrders = ServiceOrderServiceImpl.filterOrdersByString(orders, LocalDate.now().toString());
        assertTrue(filteredOrders.isEmpty());
    }

    @Test
    void testGetSOrdersByFilter() {
        var user1 = new User(Role.CLIENT, "john_doe", "password");
        user1.setName("test");
        user1.setSurname("test");
        user1.setPhone("89106791784");

        ServiceOrder order1 = new ServiceOrder(
                user1,
                new Car("Toyota", "Camry", 2020, 30000L)
        );
        order1.setStatus(OrderStatus.COMPLETE);
        var user2 = new User(Role.CLIENT, "jane_doe", "password");
        user2.setName("test2");
        user2.setSurname("test2");
        user2.setPhone("89106791785");
        ServiceOrder order2 = new ServiceOrder(
                user2,
                new Car("Honda", "Accord", 2021, 28000L)
        );
        order2.setStatus(OrderStatus.IN_PROGRESS);
        List<ServiceOrder> orders = List.of(order1, order2);

        List<ServiceOrder> filteredOrders = ServiceOrderServiceImpl.filterOrdersByString(orders, "test");
        assertEquals(1, filteredOrders.size());
        assertEquals(order1, filteredOrders.get(0));

        filteredOrders = ServiceOrderServiceImpl.filterOrdersByString(orders, OrderStatus.IN_PROGRESS.toString());
        assertEquals(1, filteredOrders.size());
        assertEquals(order2, filteredOrders.get(0));

        filteredOrders = ServiceOrderServiceImpl.filterOrdersByString(orders, LocalDate.now().toString());
        assertTrue(filteredOrders.isEmpty());
    }
}
