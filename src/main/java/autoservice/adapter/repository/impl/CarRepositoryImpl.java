package autoservice.adapter.repository.impl;

import autoservice.adapter.repository.CarRepository;
import autoservice.domen.model.Car;
import autoservice.domen.model.enums.CarState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class CarRepositoryImpl implements CarRepository {

    JdbcTemplate jdbcTemplate;

    private final RowMapper<Car> carRowMapper = (rs, rowNum) -> {
        var id = rs.getInt("id");
        var make = rs.getString("make");
        var model = rs.getString("model");
        var year = rs.getInt("year");
        var price = rs.getLong("price");
        var state = CarState.valueOf(rs.getString("state"));
        return new Car(id, state, make, model, year, price);
    };

    @Override
    public Optional<Car> create(Car car) {
        var insertSql = "INSERT INTO car_service.car (make, model, year, price, state) VALUES (?, ?, ?, ?, ?)";

        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, car.getMake());
            ps.setString(2, car.getModel());
            ps.setInt(3, car.getYear());
            ps.setLong(4, car.getPrice());
            ps.setString(5, car.getState().name());
            return ps;
        }, keyHolder);

        var generatedId = keyHolder.getKey();
        if (generatedId != null) {
            car.setId(generatedId.intValue());
            return Optional.of(car);
        }
        return Optional.empty();
    }

    @Override
    public int delete(Car car) {
        var sql = "DELETE FROM car_service.car WHERE id = ?";
        return jdbcTemplate.update(sql, car.getId());
    }

    @Override
    public int update(Car car) {
        var sql = "UPDATE car_service.car SET make = ?, model = ?, year = ?, price = ?, state = ? WHERE id = ?";
        return jdbcTemplate.update(sql, car.getMake(), car.getModel(), car.getYear(), car.getPrice(), car.getState().name(), car.getId());
    }

    @Override
    public boolean existsById(int id) {
        var sql = "SELECT COUNT(*) FROM car_service.car WHERE id = ?";
        var count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public Optional<Car> findById(int id) {
        var sql = "SELECT id, make, model, year, price, state FROM car_service.car WHERE id = ?";
        return jdbcTemplate.query(sql, carRowMapper, id).stream().findFirst();
    }

    @Override
    public Stream<Car> findAll() {
        var sql = "SELECT * FROM car_service.car";
        return jdbcTemplate.query(sql, carRowMapper).stream();
    }

    @Override
    public Stream<Car> findByFilter(Predicate<Car> predicate) {
        return findAll().filter(predicate);
    }
}

