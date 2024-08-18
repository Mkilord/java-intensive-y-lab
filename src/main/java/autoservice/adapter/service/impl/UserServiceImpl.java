package autoservice.adapter.service.impl;

import autoservice.adapter.repository.UserRepository;
import autoservice.adapter.service.RoleException;
import autoservice.adapter.service.UserService;
import autoservice.model.Role;
import autoservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public List<User> getAllUsers(Role role) throws RoleException {
        if (role.equals(Role.CLIENT)) throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        if (role.equals(Role.MANAGER)) {
            return authRepo.findByFilter(client -> client.getRole().equals(Role.CLIENT)).toList();
        }
        return authRepo.findAll();
    }

    @Override
    public Stream<User> getAllUsersStream(Role role) throws RoleException {
        return getAllUsers(role).stream();
    }

    @Override
    public Optional<User> getUserByID(Role role, int id) throws RoleException {
        if (role.equals(Role.CLIENT)) throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        if (role.equals(Role.MANAGER)) {
            return authRepo.findByFilter(client -> client.getId() == id &&
                    client.getRole().equals(Role.CLIENT)
            ).findFirst();
        }
        return authRepo.findById(id);
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
    public List<User> getUsersByFilter(Role role, Predicate<User> predicate) throws RoleException {
        return getUsersByFilterStream(role, predicate).toList();
    }

    @Override
    public Stream<User> getUsersByFilterStream(Role role, Predicate<User> predicate) throws RoleException {
        if (role.equals(Role.CLIENT)) throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        if (role.equals(Role.MANAGER)) {
            return authRepo.findByFilter(((Predicate<User>) client -> client.getRole().equals(Role.CLIENT))
                    .and(predicate));
        }
        return authRepo.findByFilter(predicate);
    }

    @Override
    public void editUser(Role role, User user) throws RoleException {
        if (!role.equals(Role.ADMIN)) throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        authRepo.update(user);
    }
}
