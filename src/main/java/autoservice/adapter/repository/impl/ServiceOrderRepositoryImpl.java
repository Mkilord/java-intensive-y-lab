package autoservice.adapter.repository.impl;

import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.repository.ServiceOrderRepository;
import autoservice.adapter.repository.UserRepository;
import autoservice.domen.model.Car;
import autoservice.domen.model.ServiceOrder;
import autoservice.domen.model.User;
import autoservice.domen.model.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Repository
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceOrderRepositoryImpl implements ServiceOrderRepository {

    UserRepository userRepository;
    CarRepository carRepository;
    JdbcTemplate jdbcTemplate;

    private final RowMapper<ServiceOrder> serviceOrderRowMapper = (ResultSet rs, int rowNum) -> {
        var id = rs.getInt("id");
        var customerId = rs.getInt("customer_id");
        var carId = rs.getInt("car_id");
        var date = rs.getDate("date");
        var status = OrderStatus.valueOf(rs.getString("status"));
        var customer = getUserById(customerId).orElseThrow(() -> new SQLException("User not found"));
        var car = getCarById(carId).orElseThrow(() -> new SQLException("Car not found"));

        return new ServiceOrder(id, date.toLocalDate(), status, customer, car);
    };

    private Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    private Optional<Car> getCarById(int id) {
        return carRepository.findById(id);
    }

    @Override
    public Optional<ServiceOrder> create(ServiceOrder order) {
        var sql = "INSERT INTO car_service.service_order (customer_id, car_id, date, status) VALUES (?, ?, ?, ?)";

        var keyHolder = new GeneratedKeyHolder();

        var rowsAffected = jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, order.getCustomer().getId());
            ps.setInt(2, order.getCar().getId());
            ps.setDate(3, Date.valueOf(order.getDate()));
            ps.setString(4, order.getStatus().name());
            return ps;
        }, keyHolder);

        if (rowsAffected > 0) {
            var keyList = keyHolder.getKeyList();
            if (!keyList.isEmpty()) {
                var firstKey = keyList.get(0);
                Integer id = (Integer) firstKey.get("id");
                if (id != null) {
                    order.setId(id);
                    return Optional.of(order);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public int delete(ServiceOrder order) {
        var sql = "DELETE FROM car_service.service_order WHERE id = ?";
        return jdbcTemplate.update(sql, order.getId());
    }

    @Override
    public int update(ServiceOrder order) {
        var sql = "UPDATE car_service.service_order SET customer_id = ?, car_id = ?, date = ?, status = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                order.getCustomer().getId(),
                order.getCar().getId(),
                Date.valueOf(order.getDate()),
                order.getStatus().name(),
                order.getId());
    }

    @Override
    public boolean existsById(int id) {
        var sql = "SELECT COUNT(*) FROM car_service.service_order WHERE id = ?";
        var count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public Optional<ServiceOrder> findById(int id) {
        var sql = "SELECT id, customer_id, car_id, date, status FROM car_service.service_order WHERE id = ?";
        return jdbcTemplate.query(sql, serviceOrderRowMapper, id).stream().findFirst();
    }

    @Override
    public Stream<ServiceOrder> findAll() {
        var sql = "SELECT id, customer_id, car_id, date, status FROM car_service.service_order";
        var orders = jdbcTemplate.query(sql, serviceOrderRowMapper);
        return orders.stream();
    }

    @Override
    public Stream<ServiceOrder> findByFilter(Predicate<ServiceOrder> predicate) {
        return findAll().filter(predicate);
    }
}
