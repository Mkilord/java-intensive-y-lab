package autoservice.adapter.service.impl;

import autoservice.adapter.repository.UserRepository;
import autoservice.adapter.service.UserService;
import autoservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {
    private final UserRepository authRepo;

    public UserServiceImpl(UserRepository authRepo) {
        this.authRepo = authRepo;
    }

    public static List<User> filterUsersByString(List<User> users, String searchString) {
        return users.stream()
                .filter(user -> {
                    boolean matchesUsername = user.getUsername().equals(searchString);
                    boolean matchesName = user.getName().equalsIgnoreCase(searchString);
                    boolean matchesSurname = user.getSurname().equalsIgnoreCase(searchString);
                    boolean matchesPhone = String.valueOf(user.getPhone()).equals(searchString);
                    return matchesUsername || matchesName || matchesSurname || matchesPhone;
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user that matches the specified filter.
     *
     * @param predicate the filter criteria
     * @return an {@link Optional} containing the first user that matches the filter, or an empty {@link Optional} if no user matches
     */
    @Override
    public Optional<User> getByFilter(Predicate<User> predicate) {
        return authRepo.findByFilter(predicate).findFirst();
    }

    /**
     * Retrieves all users from the repository.
     *
     * @return a list of all users
     */
    @Override
    public List<User> getAllUsers() {
        return authRepo.findAll();
    }

    @Override
    public Optional<User> getUserByID(int id) {
        return authRepo.findById(id);
    }


    /**
     * Retrieves all users that match the specified filter.
     *
     * @param predicate the filter criteria
     * @return a list of users that match the filter
     */
    @Override
    public List<User> getUsersByFilter(Predicate<User> predicate) {
        return authRepo.findByFilter(predicate).toList();
    }
}
