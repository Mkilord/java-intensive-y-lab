package autoservice.adapter.service.impl;

import autoservice.adapter.repository.UserRepository;
import autoservice.adapter.service.UserAuthService;
import autoservice.model.Role;
import autoservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link UserAuthService} interface.
 */
public class UserAuthServiceImpl implements UserAuthService {
    private final UserRepository authRepo;

    /**
     * Constructs a new {@code UserAuthServiceImpl} with the specified {@link UserRepository}.
     *
     * @param authRepo the repository used for user authentication data
     */
    public UserAuthServiceImpl(UserRepository authRepo) {
        this.authRepo = authRepo;
    }

    /**
     * Filters a list of users by a specified search string.
     *
     * @param users        the list of users to filter
     * @param searchString the search string to filter by
     * @return a list of users that match the search string
     */
    public static List<User> filterUsersByString(List<User> users, String searchString) {
        return users.stream()
                .filter(user -> {
                    boolean matchesUsername = user.getUsername().equalsIgnoreCase(searchString);
                    boolean matchesName = user.getName() != null && user.getName().equalsIgnoreCase(searchString);
                    boolean matchesSurname = user.getSurname() != null && user.getSurname().equalsIgnoreCase(searchString);
                    boolean matchesPhone = user.getPhone() != null && user.getPhone().equals(searchString);
                    boolean matchesRole = user.getRole().toString().equalsIgnoreCase(searchString);

                    return matchesUsername || matchesName || matchesSurname || matchesPhone || matchesRole;
                })
                .collect(Collectors.toList());
    }

    /**
     * Registers a new user with the specified role, username, and password.
     *
     * @param role     the role of the new user
     * @param username the username of the new user
     * @param password the password of the new user
     * @return an {@link Optional} containing the newly registered user if registration was successful, or an empty {@link Optional} if the username is already taken
     */
    @Override
    public Optional<User> register(Role role, String username, String password) {
        var out = getByUsername(username);
        if (out.isEmpty()) {
            var user = new User(role, username, password);
            authRepo.create(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * Logs in a user with the specified username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return an {@link Optional} containing the user if the credentials are correct, or an empty {@link Optional} if the credentials are incorrect
     */

    @Override
    public Optional<User> login(String username, String password) {
        return authRepo.findByFilter(user -> user.getUsername().equals(username)
                && user.getPassword().equals(password)).findFirst();
    }

    /**
     * Retrieves a user that matches the specified filter.
     *
     * @param predicate the filter criteria
     * @return an {@link Optional} containing the first user that matches the filter, or an empty {@link Optional} if no user matches
     */
    @Deprecated
    @Override
    public Optional<User> getByFilter(Predicate<User> predicate) {
        return authRepo.findByFilter(predicate).findFirst();
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user
     * @return an {@link Optional} containing the user if found, or an empty {@link Optional} if no user with the given username exists
     */
    @Deprecated
    @Override
    public Optional<User> getByUsername(String username) {
        return authRepo.findByFilter(user -> user.getUsername().equals(username)).findFirst();
    }
}
