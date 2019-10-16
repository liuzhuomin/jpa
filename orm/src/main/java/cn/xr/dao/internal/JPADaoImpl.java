package cn.xr.dao.internal;

import cn.xr.annotaions.LogModel;
import cn.xr.dao.JPADao;
import cn.xr.dao.Page;
import cn.xr.dao.Query;
import cn.xr.dao.exception.NotFoundException;
import cn.xr.dao.exception.PersistenceException;
import cn.xr.dao.internal.support.HibernateMetadataUtilAdapter;
import cn.xr.dao.internal.support.MetadataUtilAdapter;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.dao.jpa.JPABaseDAO;
import com.googlecode.genericdao.search.Metadata;
import com.googlecode.genericdao.search.MetadataUtil;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.jpa.JPASearchProcessor;
import com.googlecode.genericdao.search.jpa.hibernate.HibernateMetadataUtil;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
class JPADaoImpl extends JPABaseDAO implements JPADao, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(JPADaoImpl.class);

    private MetadataUtil metadataUtil;

    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    protected MetadataUtil getMetadataUtil() {
        if (metadataUtil == null) {
            metadataUtil = MetadataUtilAdapter.getInstanceForEntityManagerFactory(em().getEntityManagerFactory());
        }
        return metadataUtil;
    }

    @Override
    protected JPASearchProcessor getSearchProcessor() {
        return new JPASearchProcessor(this.getMetadataUtil());
    }

    public <T, ID extends Serializable> T get(Class<T> type, ID id) {
        T result = this.find(type, id);
        if (result == null) {
            throw new NotFoundException(type, id, null);
        }
        return result;
    }

    public <T, ID extends Serializable> List<T> get(Class<T> type, ID id, ID... rest) {
        return this.get(type, Lists.asList(checkNotNull(id), rest));
    }

    public <T, ID extends Serializable> List<T> get(Class<T> type, Iterable<ID> ids) {
        final List<T> result = Lists.newArrayList();
        for (ID it : ids) {
            try {
                result.add(this.get(type, it));
            } catch (Exception e) {
                throw new NotFoundException(type, it, e);
            }
        }
        return result;
    }

    public <T, ID extends Serializable> T find(Class<T> type, ID id) {
        return super._find(checkNotNull(type), checkNotNull(id));
    }

    public <T, ID extends Serializable> List<T> find(Class<T> type, ID id, ID... rest) {
        return this.find(type, Lists.asList(checkNotNull(id), rest));
    }

    public <T, ID extends Serializable> List<T> find(Class<T> type, Iterable<ID> ids) {
        checkNotNull(ids);
        final List<T> result = Lists.newArrayList();
        for (ID it : ids) {
            result.add(this.find(type, it));
        }
        return result;
    }

    public <T> List<T> find(Class<T> type) {
        checkNotNull(type);
        return this.list(new QueryImpl<T, T>(this, type, type));
    }

    public <T> T put(T entity) {
        checkNotNull(entity);
        try {
            _persistOrMerge(entity);
            return entity;
        } catch (Exception e) {
            throw new PersistenceException(entity, "保存", "出错!", e);
        }
    }

    /**
     * If an entity with the same ID already exists in the database, merge the
     * changes into that entity. If not persist the given entity. In either
     * case, a managed entity with the changed values is returned. It may or may
     * not be the same object as was passed in.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected <T> T _persistOrMerge(T entity) {
        if (entity == null) {
            logger.debug("entity == null");
            return null;
        }
        if (em().contains(entity)) {
            logger.debug("em().contains(entity), try to _merge");
            return _merge(entity);
        }
        Serializable id = getMetadataUtil().getId(entity);
        if (id == null) {
            logger.debug("_persist(id == null)");
            _persist(entity);
            return entity;
        }
        T prev = em().find((Class<T>) entity.getClass(), id);
        if (prev == null) {
            logger.debug("_persist(prev == null)");
            _persist(entity);
            return entity;
        } else {
            logger.debug("_merge");
            return _merge(entity);
        }
    }

    public <T> List<T> put(T entity, T... rest) {
        return this.put(Lists.asList(checkNotNull(entity), rest));
    }

    public <T> List<T> put(Iterable<? extends T> entities) {
        final List<T> result = Lists.newArrayList();
        for (T it : entities) {
            this.put(it);
            result.add(it);
        }
        return result;
    }

    public <T> T delete(T entity) {
        checkNotNull(entity);
        try {
            _removeEntity(entity);
            return entity;
        } catch (Exception e) {
            throw new PersistenceException(entity, "删除", "出错!", e);
        }
    }

    public <T> List<T> delete(T entity, T... rest) {
        return this.delete(Lists.asList(checkNotNull(entity), rest));
    }

    public <T> List<T> delete(Iterable<T> entities) {
        checkNotNull(entities);
        final List<T> result = Lists.newArrayList();
        for (T it : entities) {
            result.add(this.delete(it));
        }
        return result;
    }

    public <ID extends Serializable> void deleteById(Class<?> type, ID id) {
        checkNotNull(type);
        checkNotNull(id);
        try {
            _removeById(type, id);
        } catch (Exception e) {
            throw new PersistenceException(type, id, "删除", "出错!", e);
        }
    }

    public <ID extends Serializable> void deleteById(Class<?> type, ID id, ID... rest) {
        this.deleteById(checkNotNull(type), Lists.asList(checkNotNull(id), rest));
    }

    public <ID extends Serializable> void deleteById(Class<?> type, Iterable<ID> ids) {
        checkNotNull(ids);
        for (ID it : ids) {
            this.deleteById(type, it);
        }
    }

    @SuppressWarnings("unchecked")
    public <T, RT> List<RT> list(Query<T, RT> query) {
        return _search(checkNotNull(query.toSearch()));
    }

    public <T, RT> Page<RT> page(Query<T, RT> query) {
        checkNotNull(query);
        if (query.autoCount()) {
            @SuppressWarnings("unchecked") final SearchResult<RT> searchResult = super._searchAndCount(query
                    .toSearch());
            final Page<RT> page = Page.get(query.thePage(), query.pagesize());
            return page.data(searchResult.getTotalCount(),
                    searchResult.getResult());
        } else {
            final List<RT> results = this.list(query);
            final Page<RT> page = Page.get(query.thePage(), query.pagesize());
            return page.data(results);
        }
    }

    public <T, RT> int count(Query<T, RT> query) {
        return super._count(checkNotNull(query.toSearch()));
    }

    public <T> int count(Class<T> type) {
        return super._count(checkNotNull(type));
    }

    @SuppressWarnings("unchecked")
    public <T, RT> RT unique(Query<T, RT> query) {
        return (RT) super._searchUnique(checkNotNull(query.toSearch()));
    }

    public <T> Query<T, T> query(Class<T> entityClass) {
        return new QueryImpl<T, T>(this, entityClass, entityClass);
    }

    public <T, RT> Query<T, RT> query(Class<T> entityClass, Class<RT> resultClass) {
        return new QueryImpl<T, RT>(this, entityClass, resultClass);
    }

    public EntityManager getEntityManager() {
        return em();
    }

    public JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(MetadataUtilAdapter.getDataSource(em().getEntityManagerFactory()));
        }
        return jdbcTemplate;
    }

    @Override
    protected void _persist(Object... entities) {
        super._persist(entities);
        for (Object entity : entities) {
            sendEntityEvent(getAddEventId(), entity);
        }
    }

    @Override
    protected <T> T _merge(T entity) {
        T merge = super._merge(entity);
        sendEntityEvent(getUpdateEventId(), merge);
        return merge;
    }

    @Override
    protected boolean _removeEntity(Object entity) {
        if (entity != null) {
            if (em().contains(entity)) {
                em().remove(entity);
                sendEntityEvent(getRemoveEventId(), entity);
                return true;
            } else {
                Serializable id = getMetadataUtil().getId(entity);
                return _removeById(entity.getClass(), id);
            }
        }
        return false;
    }

    @Override
    protected boolean _removeById(Class<?> type, Serializable id) {
        boolean success = super._removeById(type, id);
        if (success) {
            sendEntityEvent(getRemoveEventId(), id);
        }
        return success;
    }

    /**
     * 发送Entity事件
     *
     * <p>当entity增删改时发送平台事件
     *
     * @param eventId 事件ID
     * @param entity
     */
    protected void sendEntityEvent(String eventId, Object entity) {
        Class<?> aClass = entity.getClass();
        boolean annotationPresent = aClass.isAnnotationPresent(LogModel.class);
        if (annotationPresent) {
            LogModel annotation = aClass.getAnnotation(LogModel.class);
            if (annotation.value()) {
                String tableName = MetadataUtilAdapter.getTableName(em(), aClass);
                Serializable id = metadataUtil.getId(entity);
                em().persist(new cn.xr.model.truth.LogModel(id+"",tableName, eventId, JSON.toJSONString(entity)));
            }
        }
    }

    protected String getAddEventId() {
        return "ADD";
    }

    protected String getUpdateEventId() {
        return "UPDATE";
    }

    protected String getRemoveEventId() {
        return "REMOVE";
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(entityManager, "entityManager must not be null!");
        super.setEntityManager(entityManager);
    }
}
