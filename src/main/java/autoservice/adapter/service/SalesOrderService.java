package autoservice.adapter.service;

import autoservice.model.SalesOrder;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Service interface for managing sales orders.
 * Provides methods to add, delete, and update the status of sales orders,
 * as well as retrieve orders based on various criteria.
 */
public interface SalesOrderService {
    boolean add(SalesOrder order);

    boolean delete(SalesOrder order);

    void complete(SalesOrder order);

    void cancel(SalesOrder order);

    void inProgress(SalesOrder order);

    Optional<SalesOrder> getOrderByFilter(Predicate<SalesOrder> predicate);

    List<SalesOrder> getAllOrders();

    List<SalesOrder> getOrdersByFilter(Predicate<SalesOrder> predicate);
}
