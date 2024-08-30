package autoservice.adapter.repository;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;


public interface CRUDRepository<T> {

    Optional<T> create(T object);

    int delete(T object);

    int update(T object);

    boolean existsById(int id);

    Optional<T> findById(int id);

    Stream<T> findAll();

    Stream<T> findByFilter(Predicate<T> predicate);

}
