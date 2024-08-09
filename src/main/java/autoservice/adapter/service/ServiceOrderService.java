package autoservice.adapter.service;

import autoservice.model.ServiceOrder;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Service interface for managing service orders.
 * Provides methods to add, delete, and update the status of service orders,
 * as well as retrieve service orders based on various criteria.
 */
public interface ServiceOrderService {
    boolean add(ServiceOrder order);

    boolean delete(ServiceOrder order);

    void complete(ServiceOrder order);

    void cancel(ServiceOrder order);

    void inProgress(ServiceOrder order);

    Optional<ServiceOrder> getOrderByFilter(Predicate<ServiceOrder> predicate);

    List<ServiceOrder> getAllOrders();

    List<ServiceOrder> getOrdersByFilter(Predicate<ServiceOrder> predicate);
}
