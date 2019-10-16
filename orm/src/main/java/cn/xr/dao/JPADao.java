package cn.xr.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

public interface JPADao {
    <T, ID extends Serializable> T get(Class<T> type, ID id);

    <T, ID extends Serializable> List<T> get(Class<T> type, ID id, ID... rest);

    <T, ID extends Serializable> List<T> get(Class<T> type, Iterable<ID> ids);

    <T, ID extends Serializable> T find(Class<T> type, ID id);

    <T> List<T> find(Class<T> type);

    <T, ID extends Serializable> List<T> find(Class<T> type, ID id, ID... rest);

    <T, ID extends Serializable> List<T> find(Class<T> type, Iterable<ID> ids);

    <T> T put(T entity);

    <T> List<T> put(T entity, T... rest);

    <T> List<T> put(Iterable<? extends T> entities);

    <T> T delete(T entity);

    <T> List<T> delete(T entity, T... rest);

    <T> List<T> delete(Iterable<T> entities);

    <ID extends Serializable> void deleteById(Class<?> type, ID id);

    <ID extends Serializable> void deleteById(Class<?> type, ID id, ID... rest);

    <ID extends Serializable> void deleteById(Class<?> type, Iterable<ID> ids);

    <T, RT> List<RT> list(Query<T, RT> query);

    <T, RT> RT unique(Query<T, RT> query);

    <T, RT> Page<RT> page(Query<T, RT> query);

    <T> int count(Class<T> type);

    <T, RT> int count(Query<T, RT> query);

    JdbcTemplate getJdbcTemplate();

    /**
     * 获取查询器.
     */
    public <T> Query<T, T> query(Class<T> entityClass);

    /**
     * 获取查询器.
     *
     * @param <RT>        查询结果返回类型参数
     * @param resultClass 返回类型
     */
    public <T, RT> Query<T, RT> query(Class<T> entityClass, Class<RT> resultClass);

    public EntityManager getEntityManager();
}
