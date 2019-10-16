package cn.xr.service.impl;


import cn.xr.dao.JPADao;
import cn.xr.model.base.JPAEntity;
import cn.xr.service.JPAManager;
import cn.xr.service.JPAManagerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component("jpaManagerFactoryImpl")
public class JPAManagerFactoryImpl implements JPAManagerFactory, BeanFactoryAware, InitializingBean {

    private ConcurrentMap<String, JPAManager<?>> managers = new ConcurrentHashMap<String, JPAManager<?>>();

    private DefaultListableBeanFactory beanFactory;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private JPADao jpaDao;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(transactionManager, "TransactionManager 未注入！");
        Assert.notNull(jpaDao, "JPADao 未注入！");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends JPAEntity<Long>> JPAManager<T> getJPAManager(Class<T> entityClass) {
        if (!managers.containsKey(entityClass.getName())) {
            JPAManager<T> gps = createJPAManager(entityClass);
            managers.put(entityClass.getName(), gps);
        }
        return (JPAManager<T>) managers.get(entityClass.getName());
    }

    @SuppressWarnings("unchecked")
    private <T extends JPAEntity<Long>> JPAManager<T> createJPAManager(Class<T> entityClass) {
        String beanName = registerBeanDefinition(entityClass);
        return (JPAManager<T>) beanFactory.getBean(beanName);
    }

    private String registerBeanDefinition(Class<?> entityClass) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DefaultJPAManagerFactoryBean.class);
        builder.addPropertyValue("transactionManager", this.transactionManager);
        builder.addPropertyValue("jpaDao", this.jpaDao);
        builder.addPropertyValue("entityClass", entityClass);

        String beanName = getDefaultBeanName(entityClass);
        beanFactory.registerBeanDefinition(beanName, builder.getBeanDefinition());
        return beanName;
    }

    private String getDefaultBeanName(Class<?> entityClass) {
        return JPAManager.class.getName() + "#" + entityClass.getSimpleName();
    }
}
