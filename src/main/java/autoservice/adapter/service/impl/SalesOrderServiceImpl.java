package autoservice.adapter.service.impl;

import autoservice.adapter.repository.OrderRepository;
import autoservice.adapter.service.SalesOrderService;
import autoservice.model.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link SalesOrderService} interface.
 */
public class SalesOrderServiceImpl implements SalesOrderService {
    private final OrderRepository orderRepo;

    /**
     * Constructs a new {@code OrderServiceImpl} with the specified {@link OrderRepository}.
     *
     * @param orderRepo the repository used for order data
     */
    public SalesOrderServiceImpl(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    /**
     * Adds a sales order to the repository.
     *
     * @param order the sales order to add
     * @return {@code true} if the sales order was added successfully, {@code false} otherwise
     * @throws RuntimeException if the customer is not a client or if the car is not for sale
     */
    @Override
    public boolean add(SalesOrder order) {
        var customer = order.getCustomer();
        if (!customer.getRole().equals(Role.CLIENT)) {
            throw new RuntimeException("Only the customer can add orders!");
        }
        var car = order.getCar();
        if (car.getState() != CarState.FOR_SALE) {
            throw new RuntimeException("The order has not been created! The car has the status: " + car.getState());
        }
        car.setState(CarState.SOLD);
        return orderRepo.create(order);
    }

    /**
     * Filters a list of sales orders by a specified search string.
     *
     * @param orders       the list of sales orders to filter
     * @param searchString the search string to filter by
     * @return a list of sales orders that match the search string
     */
    public static List<Order> filterOrdersByString(List<Order> orders, String searchString) {
        return orders.stream()
                .filter(order -> {
                    boolean matchesId = String.valueOf(order.getId()).equals(searchString);
                    boolean matchesCustomerName = order.getCustomer().getName().equalsIgnoreCase(searchString);
                    boolean matchesStatus = order.getStatus().toString().equalsIgnoreCase(searchString);
                    boolean matchesDate = order.getDate().toString().contains(searchString);

                    return matchesId || matchesCustomerName || matchesStatus || matchesDate;
                })
                .collect(Collectors.toList());
    }

    /**
     * Deletes a sales order from the repository.
     *
     * @param order the sales order to delete
     * @return {@code true} if the sales order was deleted successfully, {@code false} otherwise
     */
    @Override
    public boolean delete(SalesOrder order) {
        return orderRepo.delete(order);
    }

    /**
     * Marks a sales order as complete.
     *
     * @param order the sales order to mark as complete
     */
    @Override
    public void complete(SalesOrder order) {
        order.setStatus(OrderStatus.COMPLETE);
    }

    /**
     * Cancels a sales order and marks the associated car as for sale.
     *
     * @param order the sales order to cancel
     */
    @Override
    public void cancel(SalesOrder order) {
        var car = order.getCar();
        car.setState(CarState.FOR_SALE);
        order.setStatus(OrderStatus.CANCEL);
    }

    /**
     * Marks a sales order as in progress and marks the associated car as sold.
     *
     * @param order the sales order to mark as in progress
     */
    @Override
    public void inProgress(SalesOrder order) {
        order.getCar().setState(CarState.SOLD);
        order.setStatus(OrderStatus.IN_PROGRESS);
    }

    /**
     * Retrieves all sales orders from the repository.
     *
     * @return a list of all sales orders
     */
    @Override
    public List<SalesOrder> getAllOrders() {
        return orderRepo.findAll();
    }

    /**
     * Retrieves a sales order that matches the specified filter.
     *
     * @param predicate the filter criteria
     * @return an {@link Optional} containing the first sales order that matches the filter, or an empty {@link Optional} if no sales order matches
     */
    @Override
    public Optional<SalesOrder> getOrderByFilter(Predicate<SalesOrder> predicate) {
        return orderRepo.findByFilter(predicate).findFirst();
    }

    /**
     * Retrieves all sales orders that match the specified filter.
     *
     * @param predicate the filter criteria
     * @return a list of sales orders that match the filter
     */
    @Override
    public List<SalesOrder> getOrdersByFilter(Predicate<SalesOrder> predicate) {
        return orderRepo.findByFilter(predicate).toList();
    }
}
