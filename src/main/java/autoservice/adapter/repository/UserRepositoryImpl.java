package autoservice.adapter.repository;

import autoservice.core.model.User;
import autoservice.core.port.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
/**
 * Implementation of the {@link UserRepository} interface.
 * This class provides basic CRUD operations for {@link User} objects using an in-memory set.
 */
public class UserRepositoryImpl implements UserRepository {
    private final HashSet<User> users = new HashSet<>();
    /**
     * Adds a new {@link User} to the repository.
     *
     * @param user the user to be added
     * @return {@code true} if the user was added successfully, {@code false} otherwise
     */
    @Override
    public boolean create(User user) {
        return users.add(user);
    }
    /**
     * Removes a {@link User} from the repository.
     *
     * @param user the user to be removed
     * @return {@code true} if the user was removed successfully, {@code false} otherwise
     */
    @Override
    public boolean delete(User user) {
        return users.remove(user);
    }
    /**
     * Retrieves all users from the repository.
     *
     * @return a list of all users
     */
    @Override
    public List<User> findAll() {
        return users.stream().toList();
    }
    /**
     * Finds users in the repository that match the given filter.
     *
     * @param condition the filter to apply to the users
     * @return a stream of users that match the filter
     */
    @Override
    public Stream<User> findByFilter(Predicate<User> condition) {
        return users.stream().filter(condition);
    }

}
