package cn.xr.service.impl;

import cn.xr.dao.JPADao;
import cn.xr.model.base.JPAEntity;
import cn.xr.service.JPAManager;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;


class DefaultJPAManagerFactoryBean<T extends JPAEntity<Long>> implements FactoryBean<JPAManager<T>>,
        InitializingBean, BeanClassLoaderAware {

    private ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

    private PlatformTransactionManager transactionManager;

    private JPADao jpaDao;

    private Class<T> entityClass;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setJpaDao(JPADao jpaDao) {
        this.jpaDao = jpaDao;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public JPAManager<T> getObject() throws Exception {
        return getTargetJPAManager();
    }

    @Override
    public Class<?> getObjectType() {
        return JPAManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(transactionManager, "TransactionManager 未注入！");
        Assert.notNull(jpaDao, "JPADao 未注入！");
        Assert.notNull(entityClass, "EntityClass 未注入！");
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader;
    }

    @SuppressWarnings("unchecked")
    protected JPAManager<T> getTargetJPAManager() {
        JPAManager<T> target = createJPAManager();

        // Create proxy
        ProxyFactory result = new ProxyFactory();
        result.setTarget(target);
        result.setInterfaces(new Class[]{JPAManager.class});
        result.addAdvice(createTranscationInterceptor());

        return (JPAManager<T>) result.getProxy(classLoader);
    }

    protected JPAManager<T> createJPAManager() {
        DefaultJPAManager<T> jpaManager = new DefaultJPAManager<T>(entityClass);
        jpaManager.setJpaDao(jpaDao);
        return jpaManager;
    }

    private TransactionInterceptor createTranscationInterceptor() {
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor(transactionManager, new JPAManagerTransactionAttributeSource());
        transactionInterceptor.afterPropertiesSet();
        return transactionInterceptor;
    }
}
