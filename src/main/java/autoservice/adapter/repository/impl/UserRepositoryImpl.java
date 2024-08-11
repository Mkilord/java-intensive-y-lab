package autoservice.adapter.repository.impl;

import autoservice.adapter.config.DatabaseManager;
import autoservice.adapter.repository.CRUDRepository;
import autoservice.adapter.repository.UserRepository;
import autoservice.model.Role;
import autoservice.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Implementation of the {@link UserRepository} interface.
 * This class provides basic CRUD operations for {@link User} objects using an in-memory set.
 */
public class UserRepositoryImpl implements UserRepository {

    /**
     * Adds a new {@link User} to the repository.
     *
     * @param user the user to be added
     * @return {@code true} if the user was added successfully, {@code false} otherwise
     */
    @Override
    public boolean create(User user) {
        String sql = "INSERT INTO car_service.user (username, password, name, surname, phone, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getName());
            statement.setString(4, user.getSurname());
            statement.setString(5, user.getPhone());
            statement.setString(6, user.getRole().name());

            int rowsAffected = statement.executeUpdate();
            var isOk = rowsAffected > 0;
            if (isOk) {
                System.out.println("User create successful!");
                return true;
            }
            System.out.println("User isn't create!");
            return false;
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
            return false;
        }
    }

    /**
     * Removes a {@link User} from the repository.
     *
     * @param user the user to be removed
     * @return {@code true} if the user was removed successfully, {@code false} otherwise
     */
    @Override
    public boolean delete(User user) {
        String sql = "DELETE FROM car_service.user WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, user.getId());

            int rowsAffected = statement.executeUpdate();
            var isOk = rowsAffected > 0;
            if (isOk) {
                System.out.println("User delete successful!");
                return true;
            }
            System.out.println("User isn't delete!");
            return false;
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
            return false;
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE car_service.user SET username = ?, password = ?, name = ?, surname = ?, phone = ?, role = ? WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getName());
            statement.setString(4, user.getSurname());
            statement.setString(5, user.getPhone());
            statement.setString(6, user.getRole().name());
            statement.setInt(7, user.getId());
            int rowsAffected = statement.executeUpdate();
            var isOk = rowsAffected > 0;
            if (isOk) {
                System.out.println("User update successful!");
                return;
            }
            System.out.println("User isn't update!");
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
        }
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT id, username, password, name, surname, phone, role " +
                "FROM car_service.user WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var tempId = resultSet.getInt("id");
                    var username = resultSet.getString("username");
                    var password = resultSet.getString("password");
                    var name = resultSet.getString("name");
                    var surname = resultSet.getString("surname");
                    var phone = resultSet.getString("phone");
                    var role = Role.valueOf(resultSet.getString("role"));
                    return Optional.of(new User(tempId, role, username, password, name, surname, phone));
                }
            }
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
        }
        return Optional.empty();
    }


    /**
     * Retrieves all users from the repository.
     *
     * @return a list of all users
     */
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, name, surname, phone, role FROM car_service.user";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                var id = resultSet.getInt("id");
                var username = resultSet.getString("username");
                var password = resultSet.getString("password");
                var name = resultSet.getString("name");
                var surname = resultSet.getString("surname");
                var phone = resultSet.getString("phone");
                var role = Role.valueOf(resultSet.getString("role"));
                User user = new User(id, role, username, password, name, surname, phone);
                users.add(user);
            }
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
        }
        return users;
    }


    /**
     * Finds users in the repository that match the given filter.
     *
     * @param condition the filter to apply to the users
     * @return a stream of users that match the filter
     */
    @Override
    public Stream<User> findByFilter(Predicate<User> condition) {
        return findAll().stream().filter(condition);
    }

    @Override
    public Optional<User> getByUsernameAndPassword(String username, String password) {
        String sql = "SELECT id, username, password, name, surname, phone, role FROM car_service.user WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var id = resultSet.getInt("id");
                    var name = resultSet.getString("name");
                    var surname = resultSet.getString("surname");
                    var phone = resultSet.getString("phone");
                    var role = Role.valueOf(resultSet.getString("role"));
                    return Optional.of(new User(id, role, username, password, name, surname, phone));
                }
            }
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getByUsername(String username) {
        String sql = "SELECT id, username, password, name, surname, phone, role FROM car_service.user WHERE username = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var id = resultSet.getInt("id");
                    var password = resultSet.getString("password");
                    var name = resultSet.getString("name");
                    var surname = resultSet.getString("surname");
                    var phone = resultSet.getString("phone");
                    var role = Role.valueOf(resultSet.getString("role"));
                    return Optional.of(new User(id, role, username, password, name, surname, phone));
                }
            }
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
        }
        return Optional.empty();
    }
}
