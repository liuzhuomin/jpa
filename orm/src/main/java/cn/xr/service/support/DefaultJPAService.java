package cn.xr.service.support;

import cn.xr.dao.Page;
import cn.xr.dao.Query;
import cn.xr.model.base.JPAEntity;
import cn.xr.service.JPAManager;
import cn.xr.service.JPAManagerFactory;
import cn.xr.service.JPAService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public class DefaultJPAService<T extends JPAEntity<Long>> implements JPAService<T>, InitializingBean {

    private Class<T> entityClass;

    @Autowired
    @Qualifier("jpaManagerFactoryImpl")
    private JPAManagerFactory jpaManagerFactory;

    private JPAManager<T> manager;

    public DefaultJPAService(Class<T> entityClass) {
        if (this.entityClass == null) {
            setEntityClass();
        } else {
            this.entityClass = entityClass;
        }
    }


//
//    public DefaultJPAService(JPAManager<T> manager) {
//        this.manager = manager;
//    }

    public DefaultJPAService() {
        setEntityClass();
    }

    private void setEntityClass() {
        Type t = getClass().getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            try {
                entityClass = (Class<T>) p[0];
//                System.out.println("class :" + entityClass);
            } catch (ClassCastException e) {
                entityClass = null;
            }
        }
    }

    protected JPAManagerFactory getJPAManagerFactory() {
        return jpaManagerFactory;
    }

    @Resource
    public void setJPAManagerFactory(JPAManagerFactory jpaManagerFactory) {
        this.jpaManagerFactory = jpaManagerFactory;
    }

    @Override
    final public void afterPropertiesSet() throws Exception {
        Assert.notNull(jpaManagerFactory, "JPAManagerFactory 未注入！");
        initManager();
    }

    /**
     * 初始化相关Manager
     *
     * <p>子类可Override此方法以初始化所需的Manager。
     */
    protected void initManager() {
        manager = getJPAManagerFactory().getJPAManager(entityClass);
    }

    protected JPAManager<T> manager() {
        return manager;
    }

    @Override
    public T get(Long id) {
        return manager().get(id);
    }

    @Override
    public T getOrElse(Long id, T that) {
        return manager().getOrElse(id, that);
    }

    @Override
    public T save(T entity) {
        return manager().save(entity);
    }

    @Override
    public List<T> save(T entity, T... rest) {
        return manager().save(entity, rest);
    }

    @Override
    public List<T> save(T[] entities) {
        return manager().save(entities);
    }

    @Override
    public T update(T entity) {
        return manager().update(entity);
    }

    @Override
    public List<T> update(T entity, T... rest) {
        return manager().update(entity, rest);
    }

    @Override
    public List<T> update(T[] entities) {
        return manager().update(entities);
    }

    @Override
    public void delete(T entity) {
        manager().delete(entity);
    }

    @Override
    public void delete(T entity, T... rest) {
        manager().delete(entity, rest);
    }

    @Override
    public void delete(T[] entities) {
        manager().delete(entities);
    }

    @Override
    public void deleteById(Long id) {
        manager().deleteById(id);
    }

    @Override
    public void deleteById(Long id, Long... rest) {
        manager().deleteById(id, rest);
    }

    @Override
    public void deleteById(Long[] ids) {
        manager().deleteById(ids);
    }

    @Override
    public int count() {
        return manager().count();
    }

    @Override
    public List<T> findAll() {
        return manager().findAll();
    }

    @Override
    public Page<T> findPage() {
        return manager().findPage();
    }

    @Override
    public T find(Long id) {
        return manager().find(id);
    }

    @Override
    public List<T> findByProperty(String name, Object value) {
        return manager().findByProperty(name, value);
    }

    @Override
    public T findUniqueByProperty(String name, Object value) {
        return manager().findUniqueByProperty(name, value);
    }

    @Override
    public List<T> findByName(Object value) {
        return manager().findByName(value);
    }

    @Override
    public T findUniqueByName(Object value) {
        return manager().findUniqueByName(value);
    }

    @Override
    public Query<T, T> query() {
        return manager().query();
    }

    @Override
    public <RT> Query<T, RT> query(Class<RT> resultClass) {
        return manager().query(resultClass);
    }

    @Override
    public <RT> RT unique(Query<T, RT> query) {
        return manager().unique(query);
    }

    @Override
    public <RT> List<RT> list(Query<T, RT> query) {
        return manager().list(query);
    }

    @Override
    public <RT> Page<RT> page(Query<T, RT> query) {
        return manager().page(query);
    }

    @Override
    public <RT> int count(Query<T, RT> query) {
        return manager().count(query);
    }

    @Override
    public Class<T> getEntityClass() {
        return manager().getEntityClass();
    }

    @Override
    public EntityManager getEntityManager() {
        return manager().getEntityManager();
    }

    protected String getEntityIDs(Collection<? extends JPAEntity<Long>> entities) {
        StringBuilder sb = new StringBuilder();
        for (JPAEntity<Long> entity : entities) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(entity.getId());
        }
        return sb.toString();
    }

    public void setEntityClass(Class<T> domainType) {
        this.entityClass = domainType;
    }
}
