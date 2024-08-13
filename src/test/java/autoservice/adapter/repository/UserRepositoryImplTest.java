package autoservice.adapter.repository;

import autoservice.adapter.config.DatabaseManager;
import autoservice.adapter.repository.impl.UserRepositoryImpl;
import autoservice.model.Role;
import autoservice.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15.2")
                    .withDatabaseName("car_service")
                    .withUsername("test")
                    .withPassword("test");

    private static UserRepository userRepository;

    @BeforeAll
    public static void setUpBeforeClass() {
        postgreSQLContainer.start();

        DatabaseManager.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
        DatabaseManager.setUsername(postgreSQLContainer.getUsername());
        DatabaseManager.setPassword(postgreSQLContainer.getPassword());

        userRepository = new UserRepositoryImpl();
    }

    @BeforeEach
    public void setUp() {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            // Drop tables if they exist
            statement.execute("DROP TABLE IF EXISTS car_service.user CASCADE");

            // Recreate schema and tables
            statement.execute("CREATE SCHEMA IF NOT EXISTS car_service");

            statement.execute("CREATE TABLE car_service.user (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username VARCHAR(255) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "name VARCHAR(255), " +
                    "surname VARCHAR(255), " +
                    "phone VARCHAR(255), " +
                    "role VARCHAR(255) NOT NULL" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM car_service.user");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateUser() {
        var user = new User(Role.CLIENT, "username" + System.currentTimeMillis(), "password",
                "John","Doe","1234567890");

        boolean userCreated = userRepository.create(user);
        assertTrue(userCreated, "User should be created");

        assertNotEquals(0, user.getId(), "User ID should be generated");
    }

    @Test
    public void testDeleteUser() {
        var user = new User(Role.CLIENT, "username" + System.currentTimeMillis(), "password",
                "John","Doe","1234567890");

        boolean userCreated = userRepository.create(user);
        assertTrue(userCreated, "User should be created");

        boolean userDeleted = userRepository.delete(user);
        assertTrue(userDeleted, "User should be deleted");

        Optional<User> fetchedUser = userRepository.findById(user.getId());
        assertFalse(fetchedUser.isPresent(), "User should not be found after deletion");
    }

    @Test
    public void testUpdateUser() {
        var user = new User(Role.CLIENT, "username" + System.currentTimeMillis(), "password",
                "John","Doe","1234567890");

        boolean userCreated = userRepository.create(user);
        assertTrue(userCreated, "User should be created");

        user.setName("Jane");
        user.setSurname("Smith");
        user.setPhone("0987654321");
        userRepository.update(user);

        var fetchedUserOpt = userRepository.findById(user.getId());
        assertTrue(fetchedUserOpt.isPresent(), "User should be found");
        assertEquals("Jane", fetchedUserOpt.get().getName(), "User name should match after update");
        assertEquals("Smith", fetchedUserOpt.get().getSurname(), "User surname should match after update");
        assertEquals("0987654321", fetchedUserOpt.get().getPhone(), "User phone should match after update");
    }

    @Test
    public void testFindById() {
        var user = new User(Role.CLIENT, "username" + System.currentTimeMillis(), "password",
                "John","Doe","1234567890");

        boolean userCreated = userRepository.create(user);
        assertTrue(userCreated, "User should be created");

        var fetchedUserOpt = userRepository.findById(user.getId());
        assertTrue(fetchedUserOpt.isPresent(), "User should be found");
        assertEquals(user.getUsername(), fetchedUserOpt.get().getUsername(), "Username should match");
        assertEquals(user.getName(), fetchedUserOpt.get().getName(), "Name should match");
        assertEquals(user.getSurname(), fetchedUserOpt.get().getSurname(), "Surname should match");
        assertEquals(user.getPhone(), fetchedUserOpt.get().getPhone(), "Phone should match");
    }

    @Test
    public void testFindAll() {
        var user1 = new User(Role.CLIENT, "username" + System.currentTimeMillis(), "password",
                "John","Doe","1234567890");

        var user2 = new User(Role.MANAGER, "username2" + System.currentTimeMillis(), "password",
                "Jane","Smith","0987654321");

        boolean user1Created = userRepository.create(user1);
        boolean user2Created = userRepository.create(user2);
        assertTrue(user1Created, "First user should be created");
        assertTrue(user2Created, "Second user should be created");

        var allUsers = userRepository.findAll();
        assertEquals(2, allUsers.size(), "Should return all users");
        assertTrue(allUsers.stream().anyMatch(u -> u.getId() == user1.getId()), "First user should be present");
        assertTrue(allUsers.stream().anyMatch(u -> u.getId() == user2.getId()), "Second user should be present");
    }

    @Test
    public void testFindByFilter() {
        var user1 = new User(Role.CLIENT, "username" + System.currentTimeMillis(), "password",
                "John","Doe","1234567890");

        var user2 = new User(Role.MANAGER, "username2" + System.currentTimeMillis(), "password",
                "Jane","Smith","0987654321");

        boolean user1Created = userRepository.create(user1);
        boolean user2Created = userRepository.create(user2);

        assertTrue(user1Created, "First user should be created");
        assertTrue(user2Created, "Second user should be created");

        Predicate<User> clientFilter = u -> u.getRole() == Role.CLIENT;
        var clients = userRepository.findByFilter(clientFilter).toList();
        assertEquals(1, clients.size(), "Should return only clients");
        assertEquals(user1.getId(), clients.get(0).getId(), "Client user should match");
    }

    @Test
    public void testGetByUsernameAndPassword() {
        var user = new User(Role.CLIENT, "username" + System.currentTimeMillis(), "password",
                "John","Doe","1234567890");

        boolean userCreated = userRepository.create(user);
        assertTrue(userCreated, "User should be created");

        var fetchedUserOpt = userRepository.getByUsernameAndPassword(user.getUsername(), user.getPassword());
        assertTrue(fetchedUserOpt.isPresent(), "User should be found by username and password");
        assertEquals(user.getUsername(), fetchedUserOpt.get().getUsername(), "Username should match");
    }

    @Test
    public void testGetByUsername() {
        var user = new User(Role.CLIENT, "username" + System.currentTimeMillis(), "password",
                "John","Doe","1234567890");

        boolean userCreated = userRepository.create(user);
        assertTrue(userCreated, "User should be created");

        var fetchedUserOpt = userRepository.getByUsername(user.getUsername());
        assertTrue(fetchedUserOpt.isPresent(), "User should be found by username");
        assertEquals(user.getUsername(), fetchedUserOpt.get().getUsername(), "Username should match");
    }
}
