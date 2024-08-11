package autoservice.adapter.repository.impl;

import autoservice.adapter.config.DatabaseManager;
import autoservice.adapter.repository.CRUDRepository;
import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.repository.ServiceOrderRepository;
import autoservice.adapter.repository.UserRepository;
import autoservice.model.Car;
import autoservice.model.OrderStatus;
import autoservice.model.ServiceOrder;
import autoservice.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Implementation of the {@link ServiceOrderRepository} interface.
 * This class provides basic CRUD operations for {@link ServiceOrder} objects using an in-memory list.
 */
public class ServiceOrderRepositoryImpl implements ServiceOrderRepository {
    private final UserRepository userRepo;
    private final CarRepository carRepo;

    public ServiceOrderRepositoryImpl(UserRepository userRepo, CarRepository carRepo) {
        this.userRepo = userRepo;
        this.carRepo = carRepo;
    }

    /**
     * Adds a new {@link ServiceOrder} to the repository.
     *
     * @param serviceOrder the service order to be added
     * @return {@code true} if the service order was added successfully, {@code false} otherwise
     */
    @Override
    public boolean create(ServiceOrder serviceOrder) {
        String sql = "INSERT INTO car_service.service_order (customer_id, car_id, date, status) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, serviceOrder.getCustomer().getId());
            statement.setInt(2, serviceOrder.getCar().getId());
            statement.setDate(3, Date.valueOf(serviceOrder.getDate()));
            statement.setString(4, serviceOrder.getStatus().name());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        serviceOrder.setId(generatedId);
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
     * Removes a {@link ServiceOrder} from the repository.
     *
     * @param object the service order to be removed
     * @return {@code true} if the service order was removed successfully, {@code false} otherwise
     */
    @Override
    public boolean delete(ServiceOrder object) {
        String sql = "DELETE FROM car_service.service_order WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, object.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            }
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
        }
        return false;
    }


    @Override
    public void update(ServiceOrder object) {
        String sql = "UPDATE car_service.service_order SET customer_id = ?, car_id = ?, date = ?, status = ? WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, object.getCustomer().getId());
            statement.setInt(2, object.getCar().getId());
            statement.setDate(3, Date.valueOf(object.getDate()));
            statement.setString(4, object.getStatus().name());
            statement.setInt(5, object.getId());

            int rowsAffected = statement.executeUpdate();
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
        }
    }


    @Override
    public Optional<ServiceOrder> findById(int id) {
        String sql = "SELECT id, customer_id, car_id, date, status FROM car_service.service_order WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int serviceOrderId = resultSet.getInt("id");
                    int customerId = resultSet.getInt("customer_id");
                    int carId = resultSet.getInt("car_id");
                    LocalDate date = resultSet.getDate("date").toLocalDate();
                    String statusStr = resultSet.getString("status");
                    OrderStatus status = OrderStatus.valueOf(statusStr);

                    Optional<User> userOpt = getUserById(customerId);
                    if (userOpt.isEmpty()) return Optional.empty();

                    Optional<Car> carOpt = getCarById(carId);
                    if (carOpt.isEmpty()) return Optional.empty();

                    ServiceOrder serviceOrder = new ServiceOrder(serviceOrderId, userOpt.get(), carOpt.get(), status, date);
                    return Optional.of(serviceOrder);
                }
            }
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
        }
        return Optional.empty();
    }

    private Optional<User> getUserById(int id) {
        return userRepo.findById(id);
    }

    private Optional<Car> getCarById(int id) {
        return carRepo.findById(id);
    }


    /**
     * Retrieves all service orders from the repository.
     *
     * @return a list of all service orders
     */
    @Override
    public List<ServiceOrder> findAll() {
        List<ServiceOrder> serviceOrders = new ArrayList<>();
        String sql = "SELECT id, customer_id, car_id, date, status FROM car_service.service_order";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int serviceOrderId = resultSet.getInt("id");
                int customerId = resultSet.getInt("customer_id");
                int carId = resultSet.getInt("car_id");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                String statusStr = resultSet.getString("status");
                OrderStatus status = OrderStatus.valueOf(statusStr);

                Optional<User> userOpt = getUserById(customerId);
                if (userOpt.isEmpty()) continue;

                Optional<Car> carOpt = getCarById(carId);
                if (carOpt.isEmpty()) continue;

                ServiceOrder serviceOrder = new ServiceOrder(serviceOrderId, userOpt.get(), carOpt.get(), status, date);
                serviceOrders.add(serviceOrder);
            }
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
        }
        return serviceOrders;
    }

    /**
     * Finds service orders in the repository that match the given filter.
     *
     * @param predicate the filter to apply to the service orders
     * @return a stream of service orders that match the filter
     */
    @Override
    public Stream<ServiceOrder> findByFilter(Predicate<ServiceOrder> predicate) {
        return findAll().stream().filter(predicate);
    }
}
