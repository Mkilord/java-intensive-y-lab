package autoservice.core.port;

import autoservice.core.model.SalesOrder;
/**
 * Repository interface for managing {@link SalesOrder} objects.
 * <p>
 * This interface extends {@link CRUDRepository}, providing standard operations
 * for working with {@link SalesOrder} objects.
 *
 * @see CRUDRepository
 * @see SalesOrder
 */
public interface OrderRepository extends CRUDRepository<SalesOrder> {
}
