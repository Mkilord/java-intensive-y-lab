package autoservice.core.services;

import autoservice.core.model.Role;
import autoservice.core.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
/**
 * Service interface for user authentication and management.
 * Provides methods for user registration, login, and retrieval of users based on different criteria.
 */
public interface UserAuthService {
    /**
     * Registers a new user with the specified role, username, and password.
     *
     * @param role the role of the user
     * @param username the username of the user
     * @param password the password of the user
     * @return an {@code Optional} containing the registered user if successful, or {@code empty} if the username is already taken
     */
    Optional<User> register(Role role, String username, String password);
    /**
     * Authenticates a user with the specified username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return an {@code Optional} containing the authenticated user if the credentials are valid, or {@code empty} otherwise
     */
    Optional<User> login(String username, String password);
    /**
     * Finds a user that matches the given filter criteria.
     *
     * @param predicate the filter criteria
     * @return an {@code Optional} containing the matching user, or {@code empty} if no match was found
     */
    Optional<User> getByFilter(Predicate<User> predicate);
    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    List<User> getAllUsers();
    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user
     * @return an {@code Optional} containing the user if found, or {@code empty} if no user with the given username exists
     */
    Optional<User> getByUsername(String username);
    /**
     * Retrieves all users that match the given filter criteria.
     *
     * @param predicate the filter criteria
     * @return a list of users that match the filter criteria
     */
    List<User> getUsersByFilter(Predicate<User> predicate);

}
