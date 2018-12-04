package service;

import java.util.Set;

public interface IService<T> {

    T get(long id);
    Set<T> get();
    Set<T> page(int start, int end);

    T update(T entity);
    T add(T entity);
    void delete(T entity);

}
