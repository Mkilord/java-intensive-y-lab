package autoservice.adapter.service;

import autoservice.adapter.service.impl.UserAuthServiceImpl;
import autoservice.model.Role;
import autoservice.model.User;
import autoservice.adapter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthServiceImplTest {

    private UserRepository authRepo;
    private UserAuthService userAuthService;

    @BeforeEach
    void setUp() {
        authRepo = Mockito.mock(UserRepository.class);
        userAuthService = new UserAuthServiceImpl(authRepo);
    }

    @Test
    void testRegisterUserSuccess() {
        String username = "john_doe";
        String password = "password";
        User user = new User(Role.CLIENT, username, password);

        Mockito.when(authRepo.findByFilter(Mockito.any(Predicate.class))).thenReturn(List.of().stream());
        Mockito.when(authRepo.create(Mockito.any(User.class))).thenReturn(true);

        Optional<User> result = userAuthService.register(Role.CLIENT, username, password);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        Mockito.verify(authRepo).create(Mockito.any(User.class));
    }

    @Test
    void testRegisterUserAlreadyExists() {
        String username = "john_doe";
        String password = "password";
        User existingUser = new User(Role.CLIENT, username, password);

        Mockito.when(authRepo.findByFilter(Mockito.any(Predicate.class))).thenReturn(List.of(existingUser).stream());

        Optional<User> result = userAuthService.register(Role.CLIENT, username, password);

        assertFalse(result.isPresent());
        Mockito.verify(authRepo, Mockito.never()).create(Mockito.any(User.class));
    }

    @Test
    void testLoginSuccess() {
        String username = "john_doe";
        String password = "password";
        User user = new User(Role.CLIENT, username, password);

        Mockito.when(authRepo.findByFilter(Mockito.any(Predicate.class))).thenReturn(List.of(user).stream());

        Optional<User> result = userAuthService.login(username, password);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
    }

    @Test
    void testLoginFailure() {
        String username = "john_doe";
        String password = "wrong_password";

        Mockito.when(authRepo.findByFilter(Mockito.any(Predicate.class))).thenReturn(List.of().stream());

        Optional<User> result = userAuthService.login(username, password);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetByFilter() {
        String username = "john_doe";
        User user = new User(Role.CLIENT, username, "password");

        Mockito.when(authRepo.findByFilter(Mockito.any(Predicate.class))).thenReturn(List.of(user).stream());

        Optional<User> result = userAuthService.getByFilter(u -> u.getUsername().equals(username));

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
    }


    @Test
    void testGetByUsername() {
        String username = "john_doe";
        User user = new User(Role.CLIENT, username, "password");

        Mockito.when(authRepo.findByFilter(Mockito.any(Predicate.class))).thenReturn(List.of(user).stream());

        Optional<User> result = userAuthService.getByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
    }


    @Test
    void testFilterUsersByString() {
        User user1 = new User(Role.CLIENT, "john_doe", "password");
        user1.setName("John");
        User user2 = new User(Role.CLIENT, "jane_doe", "password");
        user2.setSurname("Doe");
        List<User> users = List.of(user1, user2);

        List<User> filteredUsers = UserAuthServiceImpl.filterUsersByString(users, "john_doe");
        assertEquals(1, filteredUsers.size());
        assertEquals(user1, filteredUsers.get(0));

        filteredUsers = UserAuthServiceImpl.filterUsersByString(users, "John");
        assertEquals(1, filteredUsers.size());
        assertEquals(user1, filteredUsers.get(0));

        filteredUsers = UserAuthServiceImpl.filterUsersByString(users, "Doe");
        assertEquals(1, filteredUsers.size());
        assertEquals(user2, filteredUsers.get(0));

        filteredUsers = UserAuthServiceImpl.filterUsersByString(users, "123456");
        assertTrue(filteredUsers.isEmpty());
    }
}
