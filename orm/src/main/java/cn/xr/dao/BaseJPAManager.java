package cn.xr.dao;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

@Transactional(rollbackFor = Exception.class)
@Component
public class BaseJPAManager implements JPAManager {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final Map<Class<? extends JPAContext>, Object> persistenceServices = Maps.newConcurrentMap();

  @Autowired
  private JPADao jpaDao;

  public <T extends JPAContext> T getPersistenceService(Class<T> clazz) {
    @SuppressWarnings("unchecked")
    T ret = (T) BaseJPAManager.persistenceServices.get(clazz);
    if (ret == null) {
      Preconditions.checkState(getJpaDao() != null, "保证JpaDao已注入");
      try {
        ret = clazz.newInstance();
        ret.setJpaDao(getJpaDao());
        BaseJPAManager.persistenceServices.put(clazz, ret);
      } catch (Exception e) {
        throw new RuntimeException("实例化" + clazz + "出错", e);
      }
    }
    return ret;
  }

  public JPADao getJpaDao() {
    return jpaDao;
  }


  public void setJpaDao(JPADao jpaDao) {
    this.jpaDao = jpaDao;
  }

  protected Logger logger() {
    return this.logger;
  }
}
