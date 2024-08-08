package autoservice.adapter.service;

import autoservice.core.model.OrderStatus;
import autoservice.core.model.ServiceOrder;
import autoservice.core.port.ServiceOrderRepository;
import autoservice.core.services.ServiceOrderService;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link ServiceOrderService} interface.
 */
public class ServiceOrderServiceImpl implements ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepo;

    /**
     * Constructs a new {@code ServiceOrderServiceImpl} with the specified {@link ServiceOrderRepository}.
     *
     * @param serviceOrderRepo the repository used for service order data
     */
    public ServiceOrderServiceImpl(ServiceOrderRepository serviceOrderRepo) {
        this.serviceOrderRepo = serviceOrderRepo;
    }

    /**
     * Adds a service order to the repository.
     *
     * @param order the service order to add
     * @return {@code true} if the service order was added successfully, {@code false} otherwise
     */
    @Override
    public boolean add(ServiceOrder order) {
        return serviceOrderRepo.create(order);
    }

    /**
     * Deletes a service order from the repository.
     *
     * @param order the service order to delete
     * @return {@code true} if the service order was deleted successfully, {@code false} otherwise
     */
    @Override
    public boolean delete(ServiceOrder order) {
        return serviceOrderRepo.delete(order);
    }

    /**
     * Marks a service order as complete.
     *
     * @param order the service order to mark as complete
     */
    @Override
    public void complete(ServiceOrder order) {
        order.setStatus(OrderStatus.COMPLETE);
    }

    /**
     * Cancels a service order.
     *
     * @param order the service order to cancel
     */
    @Override
    public void cancel(ServiceOrder order) {
        order.setStatus(OrderStatus.CANCEL);
    }

    /**
     * Marks a service order as in progress.
     *
     * @param order the service order to mark as in progress
     */
    @Override
    public void inProgress(ServiceOrder order) {
        order.setStatus(OrderStatus.IN_PROCESS);
    }

    /**
     * Retrieves all service orders from the repository.
     *
     * @return a list of all service orders
     */
    @Override
    public List<ServiceOrder> getAllSOrders() {
        return serviceOrderRepo.findAll();
    }

    /**
     * Retrieves a service order that matches the specified filter.
     *
     * @param predicate the filter criteria
     * @return an {@link Optional} containing the first service order that matches the filter, or an empty {@link Optional} if no service order matches
     */
    @Override
    public Optional<ServiceOrder> getSOrderByFilter(Predicate<ServiceOrder> predicate) {
        return serviceOrderRepo.findByFilter(predicate).findFirst();
    }

    /**
     * Filters a list of service orders by a specified search string.
     *
     * @param orders       the list of service orders to filter
     * @param searchString the search string to filter by
     * @return a list of service orders that match the search string
     */
    public static List<ServiceOrder> filterOrdersByString(List<ServiceOrder> orders, String searchString) {
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
     * Retrieves all service orders that match the specified filter.
     *
     * @param predicate the filter criteria
     * @return a list of service orders that match the filter
     */
    @Override
    public List<ServiceOrder> getSOrdersByFilter(Predicate<ServiceOrder> predicate) {
        return serviceOrderRepo.findByFilter(predicate).toList();
    }
}
