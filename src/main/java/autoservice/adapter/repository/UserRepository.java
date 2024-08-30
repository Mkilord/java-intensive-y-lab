package autoservice.adapter.repository;

import autoservice.domen.model.User;

import java.util.Optional;

public interface UserRepository extends CRUDRepository<User> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
}
