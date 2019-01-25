package manager;

import domain.viewmodel.ViewModel;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.util.stream.Stream;

public abstract class DataManager<T> {
    private final Class<T> entityClass;

    @PersistenceContext
    protected EntityManager em;

    EntityManager getEntityManager() {
        return em;
    }

    DataManager(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T get(long id) {
        return getEntityManager().find(entityClass, id);
    }

    public T create(T entity) {
        try {
            getEntityManager().persist(entity);
        } catch (ConstraintViolationException e) {
            e.getConstraintViolations().forEach(err -> System.err.println(err.toString()));
        }

        return entity;
    }

    public void update(T entity) {
        getEntityManager().merge(entity);
    }

    public void persist(T entity) {
        getEntityManager().persist(entity);
    }

    public void delete(long id) {
        getEntityManager().remove(
                getEntityManager().find(entityClass, id)
        );
    }

    public abstract Stream<T> all();

}
