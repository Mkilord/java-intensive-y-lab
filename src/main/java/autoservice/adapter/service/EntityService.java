package autoservice.adapter.service;

import java.util.List;
import java.util.function.Predicate;

public interface EntityService<T> {
    /**
     * Получает объект по его идентификатору.
     *
     * @param id идентификатор объекта, который требуется получить
     * @return объект с указанным идентификатором
     * @throws NotFoundException если объект с данным идентификатором не найден
     */
    T getById(int id) throws NotFoundException;

    /**
     * Получает объект, который соответствует указанному фильтру.
     *
     * @param predicate предикат, определяющий критерии фильтрации
     * @return объект, который соответствует фильтру
     * @throws NotFoundException если объект, соответствующий фильтру, не найден
     */
    T getEntityByFilter(Predicate<T> predicate) throws NotFoundException;

    /**
     * Получает все объекты.
     *
     * @return список всех объектов
     */
    List<T> getAll() throws NotFoundException;

    /**
     * Получает все объекты, которые соответствуют указанному фильтру.
     *
     * @param predicate предикат, определяющий критерии фильтрации
     * @return список объектов, которые соответствуют заданному фильтру
     * @throws NotFoundException если объекты не найдены
     */
    List<T> getEntitiesByFilter(Predicate<T> predicate) throws NotFoundException;

    /**
     * Ищет объекты в списке по строковому значению.
     *
     * @param entities     список объектов, в котором производится поиск
     * @param searchString строка для поиска
     * @return список объектов, которые соответствуют строке поиска
     * @throws NotFoundException если объекты не найдены
     */
    List<T> getByString(List<T> entities, String searchString) throws NotFoundException;

    /**
     * Создает новый объект.
     *
     * @param entity объект, который требуется создать
     * @return созданный объект
     * @throws NotFoundException если объекты не найдены
     */
    T create(T entity);

    /**
     * Удаляет указанный объект.
     *
     * @param entity объект, который требуется удалить
     */
    void delete(T entity);

    /**
     * Обновляет существующий объект.
     *
     * @param entity объект с обновленными данными
     */
    void update(T entity);

}
