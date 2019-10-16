package cn.xr.dao;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import java.util.List;

/**
 * 持久化实体服务接口.
 *
 * @param <T>  实体类型参数
 * @param <ID> 主键类型参数
 * @author yhy
 * @author liuliuliu
 */
@SuppressWarnings("unused")
public interface PersistenceService<T, ID> {
    /**
     * 获取实体对象.
     *
     * @param id ID
     * @return 返回对应id的实体对象.
     * @throws RuntimeException 如果未找到对应id实体.
     */
    T get(ID id);

    /**
     * 获取实体对象.
     *
     * @param id   ID
     * @param that 默认对象
     * @return 返回对应id的实体对象, 如果不存在, 返回默认对象.
     */
    T getOrElse(ID id, T that);

    /**
     * 保存实体.
     *
     * @param entity 要保存的实体对象
     * @return 返回已保存的实体对象
     */
    T save(T entity);

    /**
     * 保存实体.
     *
     * @param entity 要更新的实体
     * @param rest   其余要保存的实体对象
     * @return 返回已保存的实体对象列表
     */
    List<T> save(T entity, T... rest);

    /**
     * 保存实体.
     *
     * @param entities 要保存的实体数组
     * @return 返回已保存的实体对象列表
     */
    List<T> save(T[] entities);

    /**
     * 更新实体.
     *
     * @param entity 要更新的实体对象
     * @return 返回已更新的实体对象
     */
    T update(T entity);

    /**
     * 更新实体.
     *
     * @param entity 要更新的实体对象
     * @param rest   其余要更新的实体对象
     * @return 返回已更新的实体对象列表
     */
    List<T> update(T entity, T... rest);

    /**
     * 更新实体.
     *
     * @param entities 要更新的实体数组
     * @return 返回已更新的实体对象列表
     */
    List<T> update(T[] entities);

    /**
     * 删除实体.
     *
     * @param entity 要删除的实体对象
     */
    void delete(T entity);

    /**
     * 删除实体.
     *
     * @param entity 要删除的实体对象
     * @param rest   其余要删除的实体对象
     */
    void delete(T entity, T... rest);

    /**
     * 删除实体.
     *
     * @param entities 要删除的实体数组
     */
    void delete(T[] entities);

    /**
     * 通过Id删除实体.
     *
     * @param id 要删除的实体id
     */

    void deleteById(ID id);

    /**
     * 通过Id删除实体.
     *
     * @param id   要删除的实体id
     * @param rest 其余要删除的实体id
     */
    void deleteById(ID id, ID... rest);

    /**
     * 通过Id删除实体.
     *
     * @param ids 要删除的实体id数组
     */
    void deleteById(ID[] ids);

    /**
     * 返回总记录数.
     *
     * @return 返回查询的数量
     */
    int count();

    /**
     * 查询所有的实体表格
     *
     * @return 返回所有实体.
     */
    List<T> findAll();

    /**
     * 分页进行查询详情见{@link Page}
     *
     * @return Page<T> 返回所有实体组成的Page对象
     */
    Page<T> findPage();

    /**
     * 通过ID查找实体.
     *
     * @param id 要查找实体的ID
     * @return 返回对应ID的实体, 如果未找到则返回{@code null}
     */
    T find(ID id);

    /**
     * 根据属性值查找实体列表.
     *
     * @param name  属性名称
     * @param value 属性对应的值
     * @return 列表
     */
    List<T> findByProperty(String name, Object value);

    /**
     * 根据属性值查找唯一实体.
     *
     * @param name  属性名称
     * @param value 属性对应的值
     * @return 返回唯一实体, 如果未找到返回{@code null}
     * @throws NonUniqueResultException 如果符合条件的多于一个
     */
    T findUniqueByProperty(String name, Object value);

    /**
     * 同findByProperty("name", value);
     *
     * @param value 条件的值
     * @return 最终查询出来的结果集
     */
    List<T> findByName(Object value);

    /**
     * 同findUniqueByProperty("name", value);
     *
     * @param value 条件的值
     * @return 最终查询出来的单个结果
     * @throws NonUniqueResultException 如果符合条件的多于一个
     */
    T findUniqueByName(Object value);

    /**
     * 获取查询器.
     *
     * @return 查询器对象
     * @see Query
     */
    Query<T, T> query();

    /**
     * 通过返回的类型获取查询器.
     *
     * @param <RT>        查询结果返回类型参数
     * @param resultClass 返回类型
     * @return Query
     * @see Query
     */
    <RT> Query<T, RT> query(Class<RT> resultClass);

    /**
     * 根据{@link   Query}查询唯一的对象
     *
     * @param query 查询器对象
     * @param <RT>  查询器上标注的代表实体类型的泛型
     * @return 最终查询的唯一结果
     */
    <RT> RT unique(Query<T, RT> query);

    /**
     * 根据{@link   Query}查询对象的结果集
     *
     * @param query 查询器对象
     * @param <RT>  查询器上标注的代表实体类型的泛型
     * @return 最终查询的结果集
     */
    <RT> List<RT> list(Query<T, RT> query);

    /**
     * 根据{@link   Query}查询分页对象
     *
     * @param query 查询器对象
     * @param <RT>  查询器上标注的代表实体类型的泛型
     * @return 分页对象 {@link Page}
     * @see Page
     */
    <RT> Page<RT> page(Query<T, RT> query);

    /**
     * 根据{@link   Query}查询对象的数量
     *
     * @param query 查询器对象
     * @param <RT>  查询器上标注的代表实体类型的泛型
     * @return 按照查询条件查询的数量
     */
    <RT> int count(Query<T, RT> query);

    /**
     * 获取实体Class类型.
     *
     * @return 实体Class类型
     * @throws RuntimeException 如果无法获取实体Class类型
     */
    Class<T> getEntityClass();

//    /**
//     * 设置需要查询的字段
//     * @param fields    字段的名称
//     * @return
//     */
//    Query<T, T> expectedProperty(String...fields);
//
//    /**
//     * 设置需要排除查询的字段
//     * @param fields    字段的名称
//     * @return
//     */
//    Query<T, T> exceptionProperty(String...fields);

    /**
     * 获取实体Class类型对应的{@link EntityManager}对象
     *
     * @return {@link EntityManager}对象
     * @throws RuntimeException 如果无法获取实体Class类型
     */
    EntityManager getEntityManager();
}
