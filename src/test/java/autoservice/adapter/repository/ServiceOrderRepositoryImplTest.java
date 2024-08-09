package autoservice.adapter.repository;

import autoservice.adapter.repository.impl.ServiceOrderRepositoryImpl;
import autoservice.model.Car;
import autoservice.model.Role;
import autoservice.model.ServiceOrder;
import autoservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


class ServiceOrderRepositoryImplTest {

    private ServiceOrderRepository serviceOrderRepository;

    @BeforeEach
    void setUp() {
        serviceOrderRepository = new ServiceOrderRepositoryImpl();
    }

    @Test
    void testCreate() {
        User customer = new User(Role.CLIENT, "johndoe", "password123");
        Car car = new Car("Toyota", "Camry", 2020, 30000L);
        ServiceOrder serviceOrder = new ServiceOrder(customer, car);

        assertTrue(serviceOrderRepository.create(serviceOrder), "ServiceOrder should be added successfully");
        assertTrue(serviceOrderRepository.findAll().contains(serviceOrder), "ServiceOrder should be in the repository");
    }

    @Test
    void testDelete() {
        User customer = new User(Role.CLIENT, "johndoe", "password123");
        Car car = new Car("Toyota", "Camry", 2020, 30000L);
        ServiceOrder serviceOrder = new ServiceOrder(customer, car);
        serviceOrderRepository.create(serviceOrder);

        assertTrue(serviceOrderRepository.delete(serviceOrder), "ServiceOrder should be removed successfully");
        assertFalse(serviceOrderRepository.findAll().contains(serviceOrder), "ServiceOrder should not be in the repository");
    }

    @Test
    void testFindAll() {
        User customer1 = new User(Role.CLIENT, "johndoe", "password123");
        User customer2 = new User(Role.MANAGER, "janesmith", "password456");
        Car car1 = new Car("Toyota", "Camry", 2020, 30000L);
        Car car2 = new Car("Honda", "Civic", 2021, 25000L);
        ServiceOrder serviceOrder1 = new ServiceOrder(customer1, car1);
        ServiceOrder serviceOrder2 = new ServiceOrder(customer2, car2);

        serviceOrderRepository.create(serviceOrder1);
        serviceOrderRepository.create(serviceOrder2);

        List<ServiceOrder> serviceOrders = serviceOrderRepository.findAll();
        assertEquals(2, serviceOrders.size(), "There should be 2 service orders in the repository");
        assertTrue(serviceOrders.contains(serviceOrder1), "ServiceOrder1 should be in the repository");
        assertTrue(serviceOrders.contains(serviceOrder2), "ServiceOrder2 should be in the repository");
    }

    @Test
    void testFindByFilter() {
        User customer1 = new User(Role.CLIENT, "johndoe", "password123");
        User customer2 = new User(Role.MANAGER, "janesmith", "password456");
        Car car1 = new Car("Toyota", "Camry", 2020, 30000L);
        Car car2 = new Car("Honda", "Civic", 2021, 25000L);
        ServiceOrder serviceOrder1 = new ServiceOrder(customer1, car1);
        ServiceOrder serviceOrder2 = new ServiceOrder(customer2, car2);

        serviceOrderRepository.create(serviceOrder1);
        serviceOrderRepository.create(serviceOrder2);

        Predicate<ServiceOrder> filterCondition = serviceOrder -> "Toyota".equals(serviceOrder.getCar().getMake());
        List<ServiceOrder> filteredServiceOrders = serviceOrderRepository.findByFilter(filterCondition).collect(Collectors.toList());

        assertEquals(1, filteredServiceOrders.size(), "There should be 1 Toyota car service order in the repository");
        assertTrue(filteredServiceOrders.contains(serviceOrder1), "ServiceOrder1 should be in the filtered results");
        assertFalse(filteredServiceOrders.contains(serviceOrder2), "ServiceOrder2 should not be in the filtered results");
    }
}
