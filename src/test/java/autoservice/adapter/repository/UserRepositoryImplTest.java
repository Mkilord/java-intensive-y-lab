package autoservice.adapter.repository;

import autoservice.adapter.repository.UserRepositoryImpl;
import autoservice.core.model.Role;
import autoservice.core.model.User;
import autoservice.core.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl();
    }

    @Test
    void testCreate() {
        User user = new User(Role.CLIENT, "johndoe", "password123");

        assertTrue(userRepository.create(user), "User should be added successfully");
        assertTrue(userRepository.findAll().contains(user), "User should be in the repository");
    }

    @Test
    void testDelete() {
        User user = new User(Role.CLIENT, "johndoe", "password123");
        userRepository.create(user);

        assertTrue(userRepository.delete(user), "User should be removed successfully");
        assertFalse(userRepository.findAll().contains(user), "User should not be in the repository");
    }

    @Test
    void testFindAll() {
        User user1 = new User(Role.CLIENT, "johndoe", "password123");
        User user2 = new User(Role.MANAGER, "janesmith", "password456");

        userRepository.create(user1);
        userRepository.create(user2);

        List<User> users = userRepository.findAll();
        assertEquals(2, users.size(), "There should be 2 users in the repository");
        assertTrue(users.contains(user1), "User1 should be in the repository");
        assertTrue(users.contains(user2), "User2 should be in the repository");
    }

    @Test
    void testFindByFilter() {
        User user1 = new User(Role.CLIENT, "johndoe", "password123");
        User user2 = new User(Role.MANAGER, "janesmith", "password456");

        userRepository.create(user1);
        userRepository.create(user2);

        Predicate<User> filterCondition = user -> "johndoe".equals(user.getUsername());
        List<User> filteredUsers = userRepository.findByFilter(filterCondition).toList();

        assertEquals(1, filteredUsers.size(), "There should be 1 user with the username 'johndoe' in the repository");
        assertTrue(filteredUsers.contains(user1), "User1 should be in the filtered results");
        assertFalse(filteredUsers.contains(user2), "User2 should not be in the filtered results");
    }
}
