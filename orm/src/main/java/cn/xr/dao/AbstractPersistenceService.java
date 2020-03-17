package cn.xr.dao;

import static com.google.common.base.Objects.firstNonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base Entity Persistence Service Support.
 */
@Transactional(readOnly = true)
//@Component
public class AbstractPersistenceService<T, ID extends Serializable> implements PersistenceService<T, ID>, JPAContext {
    private static final String PROPERTY_NAME = "name2";

    protected JPADao jpaDao;

    private final Class<T> entityClass;

    public AbstractPersistenceService(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }


    @Autowired
    public void setJpaDao(JPADao jpaDao) {
        this.jpaDao = jpaDao;
    }

    public T get(ID id) {
        return jpaDao.get(getEntityClass(), id);
    }

    public T getOrElse(ID id, T that) {
        final T result = find(id);
        return firstNonNull(result, that);
    }

    @Transactional(readOnly = false)
    public T save(T entity) {
        return jpaDao.put(entity);
    }

    @Transactional(readOnly = false)
    public List<T> save(T entity, T... rest) {
        return jpaDao.put(entity, rest);
    }

    @Transactional(readOnly = false)
    public List<T> save(T[] entities) {
        return jpaDao.put(Arrays.asList(entities));
    }

    @Transactional(readOnly = false)
    public T update(T entity) {
        return jpaDao.put(entity);
    }

    @Transactional(readOnly = false)
    public List<T> update(T entity, T... rest) {
        return jpaDao.put(entity, rest);
    }

    @Transactional(readOnly = false)
    public List<T> update(T[] entities) {
        return jpaDao.put(Arrays.asList(entities));
    }

    @Transactional(readOnly = false)
    public void delete(T entity) {
        jpaDao.delete(entity);
    }

    @Transactional(readOnly = false)
    public void delete(T entity, T... rest) {
        jpaDao.delete(entity, rest);
    }

    @Transactional(readOnly = false)
    public void delete(T[] entities) {
        jpaDao.delete(Arrays.asList(entities));
    }

    @Transactional(readOnly = false)
    public void deleteById(ID id) {
        jpaDao.deleteById(getEntityClass(), id);
    }

    @Transactional(readOnly = false)
    public void deleteById(ID id, ID... rest) {
        jpaDao.deleteById(getEntityClass(), id, rest);
    }

    @Transactional(readOnly = false)
    public void deleteById(ID[] ids) {
        jpaDao.deleteById(getEntityClass(), Arrays.asList(ids));
    }

    public int count() {
        return jpaDao.count(getEntityClass());
    }

    public List<T> findAll() {
        return jpaDao.find(getEntityClass());
    }

    public Page<T> findPage() {
        return query().page(1, Integer.MAX_VALUE).page();
    }

    public T find(ID id) {
        return jpaDao.find(getEntityClass(), id);
    }

    public List<T> findByProperty(String name, Object value) {
        return query().filterEqual(name, value).list();
    }

    public T findUniqueByProperty(String name, Object value) {
        return query().filterEqual(name, value).unique();
    }

    public List<T> findByName(Object value) {
        return findByProperty(PROPERTY_NAME, value);
    }

    public T findUniqueByName(Object value) {
        return findUniqueByProperty(PROPERTY_NAME, value);
    }

    public <RT> RT unique(Query<T, RT> query) {
        return jpaDao.unique(query);
    }

    public <RT> List<RT> list(Query<T, RT> query) {
        return jpaDao.list(query);
    }

    public <RT> Page<RT> page(Query<T, RT> query) {
        return jpaDao.page(query);
    }

    public <RT> int count(Query<T, RT> query) {
        return jpaDao.count(query);
    }

    public Query<T, T> query() {
        return jpaDao.query(this.getEntityClass());
    }

    public <RT> Query<T, RT> query(Class<RT> resultClass) {
        return jpaDao.query(this.getEntityClass(), resultClass);
    }

    public EntityManager getEntityManager() {
        return jpaDao.getEntityManager();
    }
}
