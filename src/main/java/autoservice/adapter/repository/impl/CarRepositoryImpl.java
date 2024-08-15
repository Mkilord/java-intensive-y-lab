package autoservice.adapter.repository.impl;

import autoservice.adapter.config.DatabaseManager;
import autoservice.adapter.repository.CRUDRepository;
import autoservice.adapter.repository.CarRepository;
import autoservice.model.Car;
import autoservice.model.CarState;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static autoservice.adapter.repository.CRUDRepository.getSQLError;

/**
 * Implementation of the {@link CarRepository} interface.
 * This class provides basic CRUD operations for {@link Car} objects using an in-memory list.
 */
public class CarRepositoryImpl implements CarRepository {

    /**
     * Adds a new {@link Car} to the repository.
     *
     * @param car the car to be added
     * @return {@code true} if the car was added successfully, {@code false} otherwise
     */
    @Override
    public boolean create(Car car) {
        String sql = "INSERT INTO car_service.car (make, model, year, price, state) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, car.getMake());
            stmt.setString(2, car.getModel());
            stmt.setInt(3, car.getYear());
            stmt.setLong(4, car.getPrice());
            stmt.setString(5, car.getState().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        car.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            getSQLError(e);
        }
        return false;
    }


    /**
     * Removes a {@link Car} from the repository.
     *
     * @param car the car to be removed
     * @return {@code true} if the car was removed successfully, {@code false} otherwise
     */

    @Override
    public boolean delete(Car car) {
        String sql = "DELETE FROM car_service.car WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, car.getId());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            getSQLError(e);
            return false;
        }
    }

    @Override
    public void update(Car car) {
        String sql = "UPDATE car_service.car SET make = ?, model = ?, year = ?, price = ?, state = ? WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, car.getMake());
            statement.setString(2, car.getModel());
            statement.setInt(3, car.getYear());
            statement.setLong(4, car.getPrice());
            statement.setString(5, car.getState().name());
            statement.setLong(6, car.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            getSQLError(e);
        }
    }

    @Override
    public Optional<Car> findById(int id) {
        String sql = "SELECT id, make, model, year, price, state FROM car_service.car WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var tempId = resultSet.getInt("id");
                    var make = resultSet.getString("make");
                    var model = resultSet.getString("model");
                    var year = resultSet.getInt("year");
                    var price = resultSet.getLong("price");
                    var state = CarState.valueOf(resultSet.getString("state"));
                    return Optional.of(new Car(tempId, state, make, model, year, price));
                }
            }
        } catch (SQLException e) {
            CRUDRepository.getSQLError(e);
        }
        return Optional.empty();
    }


    /**
     * Retrieves all cars from the repository.
     *
     * @return a list of all cars
     */
    public List<Car> findAll() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM car_service.car";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                var id = resultSet.getInt("id");
                var make = resultSet.getString("make");
                var model = resultSet.getString("model");
                var year = resultSet.getInt("year");
                var price = resultSet.getLong("price");
                var state = CarState.valueOf(resultSet.getString("state"));
                Car car = new Car(id, state, make, model, year, price);
                cars.add(car);
            }
        } catch (SQLException e) {
            getSQLError(e);
        }
        return cars;
    }

    /**
     * Finds cars in the repository that match the given filter.
     *
     * @param predicate the filter to apply to the cars
     * @return a stream of cars that match the filter
     */
    @Override
    public Stream<Car> findByFilter(Predicate<Car> predicate) {
        return findAll().stream().filter(predicate);
    }
}
