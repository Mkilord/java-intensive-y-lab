package autoservice.adapter.repository.impl;

import autoservice.adapter.config.DatabaseManager;
import autoservice.adapter.repository.CRUDRepository;
import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.repository.OrderRepository;
import autoservice.adapter.repository.UserRepository;
import autoservice.model.Car;
import autoservice.model.OrderStatus;
import autoservice.model.SalesOrder;
import autoservice.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Implementation of the {@link OrderRepository} interface.
 * This class provides basic CRUD operations for {@link SalesOrder} objects using an in-memory list.
 */
public class OrderRepositoryImpl implements OrderRepository {

    private final UserRepository userRepository;
    private final CarRepository carRepository;

    public OrderRepositoryImpl(UserRepository userRepository, CarRepository carRepository) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    @Override
    public boolean create(SalesOrder order) {
        String sql = "INSERT INTO car_service.sales_order (customer_id, car_id, date, status) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, order.getCustomer().getId());
            statement.setInt(2, order.getCar().getId());
            statement.setDate(3, Date.valueOf(String.valueOf(order.getDate())));
            statement.setString(4, order.getStatus().name());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        order.setId(generatedId);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
        }
        return false;
    }


    /**
     * Removes a {@link SalesOrder} from the repository.
     *
     * @param order the sales order to be removed
     * @return {@code true} if the sales order was removed successfully, {@code false} otherwise
     */
    @Override
    public boolean delete(SalesOrder order) {
        String sql = "DELETE FROM car_service.sales_order WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, order.getId());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
            return false;
        }
    }


    @Override
    public void update(SalesOrder order) {
        String sql = "UPDATE car_service.sales_order SET customer_id = ?, car_id = ?, date = ?, status = ? WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, order.getCustomer().getId());
            statement.setInt(2, order.getCar().getId());
            statement.setDate(3, Date.valueOf(String.valueOf(order.getDate())));
            statement.setString(4, order.getStatus().name());
            statement.setInt(5, order.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
        }
    }


    @Override
    public Optional<SalesOrder> findById(int id) {
        String sql = "SELECT id, customer_id, car_id, date, status FROM car_service.sales_order WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var orderId = resultSet.getInt("id");
                    var customerId = resultSet.getInt("customer_id");
                    var carId = resultSet.getInt("car_id");
                    var date = resultSet.getDate("date").toLocalDate();
                    var statusStr = resultSet.getString("status");
                    var status = OrderStatus.valueOf(statusStr);

                    var userOpt = getUserById(customerId);
                    if (userOpt.isEmpty()) return Optional.empty();

                    var carOpt = getCarById(carId);
                    if (carOpt.isEmpty()) return Optional.empty();

                    var order = new SalesOrder(orderId, date, status, userOpt.get(), carOpt.get());
                    return Optional.of(order);
                }
            }
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
        }
        return Optional.empty();
    }

    private Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    private Optional<Car> getCarById(int id) {
        return carRepository.findById(id);
    }

    /**
     * Retrieves all sales orders from the repository.
     *
     * @return a list of all sales orders
     */
    @Override
    public List<SalesOrder> findAll() {
        String sql = "SELECT id, customer_id, car_id, date, status FROM car_service.sales_order";
        List<SalesOrder> salesOrders = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                var orderId = resultSet.getInt("id");
                var customerId = resultSet.getInt("customer_id");
                var carId = resultSet.getInt("car_id");
                var date = resultSet.getDate("date").toLocalDate();
                var statusStr = resultSet.getString("status");
                OrderStatus status = OrderStatus.valueOf(statusStr);

                var userOpt = getUserById(customerId);
                if (userOpt.isEmpty()) continue;

                var carOpt = getCarById(carId);
                if (carOpt.isEmpty()) continue;

                var order = new SalesOrder(orderId, date, status, userOpt.get(), carOpt.get());
                salesOrders.add(order);
            }
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
        }

        return salesOrders;
    }

    /**
     * Finds sales orders in the repository that match the given filter.
     *
     * @param predicate the filter to apply to the sales orders
     * @return a stream of sales orders that match the filter
     */
    @Override
    public Stream<SalesOrder> findByFilter(Predicate<SalesOrder> predicate) {
        return findAll().stream().filter(predicate);
    }
}
