package autoservice.adapter.repository;

import autoservice.core.model.ServiceOrder;
import autoservice.core.port.ServiceOrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
/**
 * Implementation of the {@link ServiceOrderRepository} interface.
 * This class provides basic CRUD operations for {@link ServiceOrder} objects using an in-memory list.
 */
public class ServiceOrderRepositoryImpl implements ServiceOrderRepository {
    private final List<ServiceOrder> serviceOrders = new ArrayList<>();
    /**
     * Adds a new {@link ServiceOrder} to the repository.
     *
     * @param object the service order to be added
     * @return {@code true} if the service order was added successfully, {@code false} otherwise
     */
    @Override
    public boolean create(ServiceOrder object) {
        return serviceOrders.add(object);
    }
    /**
     * Removes a {@link ServiceOrder} from the repository.
     *
     * @param object the service order to be removed
     * @return {@code true} if the service order was removed successfully, {@code false} otherwise
     */
    @Override
    public boolean delete(ServiceOrder object) {
        return serviceOrders.remove(object);
    }
    /**
     * Retrieves all service orders from the repository.
     *
     * @return a list of all service orders
     */
    @Override
    public List<ServiceOrder> findAll() {
        return serviceOrders;
    }
    /**
     * Finds service orders in the repository that match the given filter.
     *
     * @param predicate the filter to apply to the service orders
     * @return a stream of service orders that match the filter
     */
    @Override
    public Stream<ServiceOrder> findByFilter(Predicate<ServiceOrder> predicate) {
        return serviceOrders.stream().filter(predicate);
    }
}
