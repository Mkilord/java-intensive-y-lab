package autoservice.adapter.repository.impl;

import autoservice.adapter.repository.UserRepository;
import autoservice.domen.model.User;
import autoservice.domen.model.enums.Role;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRepositoryImpl implements UserRepository {

    JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        var id = rs.getInt("id");
        var username = rs.getString("username");
        var password = rs.getString("password");
        var name = rs.getString("name");
        var surname = rs.getString("surname");
        var phone = rs.getString("phone");
        var role = Role.valueOf(rs.getString("role"));
        var email = rs.getString("email");
        return new User(id, role, email, username, password, name, surname, phone);
    };

    @Override
    public Optional<User> create(User user) {
        if (user.getId() == null || !existsById(user.getId())) {
            var insertSql = "INSERT INTO car_service.user (username, password, name, surname, phone, role, email) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            var keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getName());
                ps.setString(4, user.getSurname());
                ps.setString(5, user.getPhone());
                ps.setString(6, user.getRole().name());
                ps.setString(7, user.getEmail());
                return ps;
            }, keyHolder);

            var generatedId = keyHolder.getKey();
            if (generatedId != null) {
                user.setId(generatedId.intValue());
            }
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public int delete(User user) {
        var sql = "DELETE FROM car_service.user WHERE id = ?";
        return jdbcTemplate.update(sql, user.getId());
    }

    @Override
    public boolean existsById(int id) {
        var sql = "SELECT COUNT(*) FROM car_service.user WHERE id = ?";
        var count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public int update(User user) {
        var sql = "UPDATE car_service.user SET username = ?, password = ?, name = ?, surname = ?, phone = ?, role = ?, email = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getName(),
                user.getSurname(),
                user.getPhone(),
                user.getRole().name(),
                user.getEmail(),
                user.getId());
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT id, email, username, password, name, surname, phone, role FROM car_service.user WHERE id = ?";
        return jdbcTemplate.query(sql, userRowMapper, id).stream().findFirst();
    }

    @Override
    public Stream<User> findAll() {
        var sql = "SELECT id, username, password, name, surname, phone, role, email FROM car_service.user";
        return jdbcTemplate.query(sql, userRowMapper).stream();
    }

    @Override
    public Stream<User> findByFilter(Predicate<User> condition) {
        return findAll().filter(condition);
    }

    public boolean existsByUsername(String username) {
        var sql = "SELECT COUNT(*) FROM car_service.user WHERE username = ?";
        var count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email) {
        var sql = "SELECT COUNT(*) FROM car_service.user WHERE email = ?";
        var count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        var sql = "SELECT id, role, email, username, password, name, surname, phone FROM car_service.user WHERE username = ?";
        return jdbcTemplate.query(sql, userRowMapper, username)
                .stream()
                .findFirst();
    }
}
