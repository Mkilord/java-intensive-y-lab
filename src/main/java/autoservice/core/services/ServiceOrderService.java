package autoservice.core.services;

import autoservice.core.model.ServiceOrder;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
/**
 * Service interface for managing service orders.
 * Provides methods to add, delete, and update the status of service orders,
 * as well as retrieve service orders based on various criteria.
 */
public interface ServiceOrderService {
    /**
     * Adds a new service order.
     *
     * @param order the service order to add
     * @return {@code true} if the order was successfully added; {@code false} otherwise
     */
    boolean add(ServiceOrder order);
    /**
     * Deletes an existing service order.
     *
     * @param order the service order to delete
     * @return {@code true} if the order was successfully deleted; {@code false} otherwise
     */
    boolean delete(ServiceOrder order);
    /**
     * Marks a service order as complete.
     *
     * @param order the service order to mark as complete
     */
    void complete(ServiceOrder order);
    /**
     * Cancels a service order.
     *
     * @param order the service order to cancel
     */
    void cancel(ServiceOrder order);
    /**
     * Sets the status of a service order to in progress.
     *
     * @param order the service order to mark as in progress
     */
    void inProgress(ServiceOrder order);
    /**
     * Retrieves all service orders.
     *
     * @return a list of all service orders
     */
    List<ServiceOrder> getAllSOrders();
    /**
     * Finds a service order that matches the given filter criteria.
     *
     * @param predicate the filter criteria
     * @return an {@code Optional} containing the matching service order, or {@code empty} if no match was found
     */
    Optional<ServiceOrder> getSOrderByFilter(Predicate<ServiceOrder> predicate);
    /**
     * Retrieves all service orders that match the given filter criteria.
     *
     * @param predicate the filter criteria
     * @return a list of service orders that match the filter criteria
     */
    List<ServiceOrder> getSOrdersByFilter(Predicate<ServiceOrder> predicate);

}
