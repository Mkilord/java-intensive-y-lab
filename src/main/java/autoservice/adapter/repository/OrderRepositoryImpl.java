package autoservice.adapter.repository;

import autoservice.core.model.SalesOrder;
import autoservice.core.port.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
/**
 * Implementation of the {@link OrderRepository} interface.
 * This class provides basic CRUD operations for {@link SalesOrder} objects using an in-memory list.
 */
public class OrderRepositoryImpl implements OrderRepository {
    private final List<SalesOrder> salesOrders = new ArrayList<>();

    /**
     * Adds a new {@link SalesOrder} to the repository.
     *
     * @param object the sales order to be added
     * @return {@code true} if the sales order was added successfully, {@code false} otherwise
     */
    @Override
    public boolean create(SalesOrder object) {
        return salesOrders.add(object);
    }
    /**
     * Removes a {@link SalesOrder} from the repository.
     *
     * @param object the sales order to be removed
     * @return {@code true} if the sales order was removed successfully, {@code false} otherwise
     */
    @Override
    public boolean delete(SalesOrder object) {
        return salesOrders.remove(object);
    }
    /**
     * Retrieves all sales orders from the repository.
     *
     * @return a list of all sales orders
     */
    @Override
    public List<SalesOrder> findAll() {
        return salesOrders;
    }
    /**
     * Finds sales orders in the repository that match the given filter.
     *
     * @param predicate the filter to apply to the sales orders
     * @return a stream of sales orders that match the filter
     */
    @Override
    public Stream<SalesOrder> findByFilter(Predicate<SalesOrder> predicate) {
        return salesOrders.stream().filter(predicate);
    }
}
