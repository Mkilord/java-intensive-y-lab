package autoservice.core.services;

import autoservice.core.model.SalesOrder;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
/**
 * Service interface for managing sales orders.
 * Provides methods to add, delete, and update the status of sales orders,
 * as well as retrieve orders based on various criteria.
 */
public interface OrderService {
    /**
     * Adds a new sales order.
     *
     * @param salesOrder the sales order to add
     * @return {@code true} if the order was successfully added; {@code false} otherwise
     */
    boolean add(SalesOrder salesOrder);
    /**
     * Deletes an existing sales order.
     *
     * @param salesOrder the sales order to delete
     * @return {@code true} if the order was successfully deleted; {@code false} otherwise
     */
    boolean delete(SalesOrder salesOrder);
    /**
     * Marks a sales order as complete.
     *
     * @param salesOrder the sales order to mark as complete
     */
    void complete(SalesOrder salesOrder);
    /**
     * Cancels a sales order.
     *
     * @param salesOrder the sales order to cancel
     */
    void cancel(SalesOrder salesOrder);
    /**
     * Sets the status of a sales order to in progress.
     *
     * @param salesOrder the sales order to mark as in progress
     */
    void inProgress(SalesOrder salesOrder);
    /**
     * Retrieves all sales orders.
     *
     * @return a list of all sales orders
     */
    List<SalesOrder> getAllOrders();
    /**
     * Finds a sales order that matches the given filter criteria.
     *
     * @param predicate the filter criteria
     * @return an {@code Optional} containing the matching sales order, or {@code empty} if no match was found
     */
    Optional<SalesOrder> getOrderByFilter(Predicate<SalesOrder> predicate);
    /**
     * Retrieves all sales orders that match the given filter criteria.
     *
     * @param predicate the filter criteria
     * @return a list of sales orders that match the filter criteria
     */
    List<SalesOrder> getOrdersByFilter(Predicate<SalesOrder> predicate);

}
