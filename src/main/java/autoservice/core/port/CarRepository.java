package autoservice.core.port;

import autoservice.core.model.Car;
/**
 * Repository interface for managing {@link Car} objects.
 * <p>
 * This interface extends {@link CRUDRepository}, providing standard operations
 * for working with {@link Car} objects.
 *
 * @see CRUDRepository
 * @see Car
 */
public interface CarRepository extends CRUDRepository<Car>{}
