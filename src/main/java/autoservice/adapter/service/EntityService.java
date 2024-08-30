package autoservice.adapter.service;

import autoservice.domen.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface EntityService<T> {
    T getById(int id);

    T getEntityByFilter(Predicate<T> predicate);

    List<T> getAll();

    List<T> getEntitiesByFilter(Predicate<T> predicate);

    List<T> getByString(List<T> entities, String searchString);

    T create(T entity);
    void delete(T entity);
    void update(T entity);
}
