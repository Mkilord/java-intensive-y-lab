package autoservice.core.port;

import autoservice.core.model.User;
/**
 * Repository interface for managing {@link User} objects.
 * <p>
 * This interface extends {@link CRUDRepository}, providing standard operations
 * for working with {@link User} objects.
 *
 * @see CRUDRepository
 * @see User
 */
public interface UserRepository extends CRUDRepository<User> {}
