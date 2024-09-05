package autoservice.adapter.repository;

import autoservice.domen.model.User;

import java.util.Optional;

public interface UserRepository extends CRUDRepository<User> {

    /**
     * Проверяет, существует ли пользователь с указанным именем пользователя.
     *
     * @param username имя пользователя, которое требуется проверить
     * @return {@code true}, если пользователь с данным именем существует, {@code false} в противном случае
     */
    boolean existsByUsername(String username);

    /**
     * Проверяет, существует ли пользователь с указанным адресом электронной почты.
     *
     * @param email адрес электронной почты, который требуется проверить
     * @return {@code true}, если пользователь с данным адресом электронной почты существует, {@code false} в противном случае
     */
    boolean existsByEmail(String email);

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username имя пользователя, по которому требуется найти пользователя
     * @return объект {@link Optional}, содержащий найденного пользователя, если таковой существует, или пустое значение,
     *         если пользователь с данным именем не найден
     */
    Optional<User> findByUsername(String username);
}
