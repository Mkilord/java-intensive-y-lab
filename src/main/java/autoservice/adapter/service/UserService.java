package autoservice.adapter.service;

import autoservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface UserService {
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

//    /**
//     * Retrieves a user by their username.
//     *
//     * @param username the username of the user
//     * @return an {@code Optional} containing the user if found, or {@code empty} if no user with the given username exists
//     */
//    Optional<User> getByUsername(String username);
    Optional<User> getUserByID(int id);
    /**
     * Retrieves all users that match the given filter criteria.
     *
     * @param predicate the filter criteria
     * @return a list of users that match the filter criteria
     */
    List<User> getUsersByFilter(Predicate<User> predicate);
}
