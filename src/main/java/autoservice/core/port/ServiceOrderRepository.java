package autoservice.core.port;

import autoservice.core.model.ServiceOrder;
/**
 * Repository interface for managing {@link ServiceOrder} objects.
 * <p>
 * This interface extends {@link CRUDRepository}, providing standard operations
 * for working with {@link ServiceOrder} objects.
 *
 * @see CRUDRepository
 * @see ServiceOrder
 */
public interface ServiceOrderRepository extends CRUDRepository<ServiceOrder>{
}
