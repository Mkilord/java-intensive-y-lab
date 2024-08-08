package autoservice.adapter.service;

import autoservice.core.model.CarState;
import autoservice.core.model.OrderStatus;
import autoservice.core.model.Role;
import autoservice.core.model.SalesOrder;
import autoservice.core.port.OrderRepository;
import autoservice.core.services.OrderService;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/**
 * Implementation of the {@link OrderService} interface.
 */
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepo;

    /**
     * Constructs a new {@code OrderServiceImpl} with the specified {@link OrderRepository}.
     *
     * @param orderRepo the repository used for order data
     */
    public OrderServiceImpl(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }
    /**
     * Adds a sales order to the repository.
     *
     * @param salesOrder the sales order to add
     * @return {@code true} if the sales order was added successfully, {@code false} otherwise
     * @throws RuntimeException if the customer is not a client or if the car is not for sale
     */
    @Override
    public boolean add(SalesOrder salesOrder) {
        var customer = salesOrder.getCustomer();
        if (!customer.getRole().equals(Role.CLIENT)) {
            throw new RuntimeException("Only the customer can add orders!");
        }
        var car = salesOrder.getCar();
        if (car.getState() != CarState.FOR_SALE) {
            throw new RuntimeException("The order has not been created! The car has the status: " + car.getState());
        }
        car.setState(CarState.SOLD);
        return orderRepo.create(salesOrder);
    }
    /**
     * Filters a list of sales orders by a specified search string.
     *
     * @param orders the list of sales orders to filter
     * @param searchString the search string to filter by
     * @return a list of sales orders that match the search string
     */
    public static List<SalesOrder> filterOrdersByString(List<SalesOrder> orders, String searchString) {
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
     * @param salesOrder the sales order to delete
     * @return {@code true} if the sales order was deleted successfully, {@code false} otherwise
     */
    @Override
    public boolean delete(SalesOrder salesOrder) {
        return orderRepo.delete(salesOrder);
    }
    /**
     * Marks a sales order as complete.
     *
     * @param salesOrder the sales order to mark as complete
     */
    @Override
    public void complete(SalesOrder salesOrder) {
        salesOrder.setStatus(OrderStatus.COMPLETE);
    }
    /**
     * Cancels a sales order and marks the associated car as for sale.
     *
     * @param salesOrder the sales order to cancel
     */
    @Override
    public void cancel(SalesOrder salesOrder) {
        var car = salesOrder.getCar();
        car.setState(CarState.FOR_SALE);
        salesOrder.setStatus(OrderStatus.CANCEL);
    }
    /**
     * Marks a sales order as in progress and marks the associated car as sold.
     *
     * @param salesOrder the sales order to mark as in progress
     */
    @Override
    public void inProgress(SalesOrder salesOrder) {
        salesOrder.getCar().setState(CarState.SOLD);
        salesOrder.setStatus(OrderStatus.IN_PROCESS);
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
