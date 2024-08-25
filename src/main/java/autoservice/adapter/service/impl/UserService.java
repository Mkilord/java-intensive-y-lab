package autoservice.adapter.service.impl;

import autoservice.adapter.repository.UserRepository;
import autoservice.adapter.service.NotFoundException;
import autoservice.adapter.service.EntityService;
import autoservice.domen.model.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements EntityService<User> {

    UserRepository repository;

    @Override
    public List<User> getByString(List<User> users, String searchString) {
        return users.stream().filter(user -> {
            boolean matchesUsername = user.getUsername().equals(searchString);
            boolean matchesName = user.getName().equalsIgnoreCase(searchString);
            boolean matchesSurname = user.getSurname().equalsIgnoreCase(searchString);
            boolean matchesPhone = String.valueOf(user.getPhone()).equals(searchString);
            return matchesUsername || matchesName || matchesSurname || matchesPhone;
        }).toList();
    }

    @Override
    public User getById(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(NotFoundException.MSG));
    }

    @Override
    public User getEntityByFilter(Predicate<User> predicate) {
        return repository.findByFilter(predicate).findFirst().orElseThrow(
                () -> new NotFoundException(NotFoundException.MSG));
    }

    @Override
    public List<User> getAll() {
        var users = repository.findAll().toList();
        if (users.isEmpty()) throw new NotFoundException(NotFoundException.MSG);
        return users;
    }

    @Override
    public List<User> getEntitiesByFilter(Predicate<User> predicate) {
        var users = repository.findByFilter(predicate).toList();
        if (users.isEmpty()) throw new NotFoundException(NotFoundException.MSG);
        return users;
    }

    @Override
    public void delete(User user) {
        var exist = repository.existsById(user.getId());
        if (!exist) {
            throw new NotFoundException("Пользователь для удаления не найден");
        }
        repository.delete(user);
    }

    @Override
    public void update(User user) {
        var exist = repository.existsById(user.getId());
        if (!exist) {
            throw new NotFoundException("Пользователь для обновления не найден");
        }
        repository.update(user);
    }

    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    @Override
    public User create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new NotFoundException("Пользователь с таким именем уже существует");
        }
        if (repository.existsByEmail(user.getEmail())) {
            throw new NotFoundException("Пользователь с таким email уже существует");
        }
        return repository.create(user).orElseThrow(() -> new RuntimeException("Не удалось создать пользователя!"));
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    public User getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }
}
