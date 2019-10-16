package cn.xr.service.support;

import static org.springframework.core.GenericTypeResolver.resolveTypeArguments;

import cn.xr.model.base.JPAEntity;
import cn.xr.service.JPAManagerFactory;
import cn.xr.service.JPAService;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;


//@Component
public class JPAServiceFactoryBean<T extends JPAService<S>, S extends JPAEntity<Long>> implements FactoryBean<T>, BeanClassLoaderAware {

	private ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

	private JPAManagerFactory jpaManagerFactory;

	private Class<? extends T> serviceInterface;

	private Object customImplementation;

	@Autowired
	public void setJPAManagerFactory(JPAManagerFactory jpaManagerFactory) {
		this.jpaManagerFactory = jpaManagerFactory;
	}

	@Required
	public void setServiceInterface(Class<? extends T> serviceInterface) {
		Assert.notNull(serviceInterface);
		this.serviceInterface = serviceInterface;
	}

	public void setCustomImplementation(Object customImplementation) {
		this.customImplementation = customImplementation;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader;
	}

	@Override
	public T getObject() throws Exception {
		return createJPAService();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends T> getObjectType() {
		return (Class<? extends T>) (null == serviceInterface ? JPAService.class : serviceInterface);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@SuppressWarnings("unchecked")
	private T createJPAService() throws Exception {
		if (customImplementation != null) {
			return (T) customImplementation;
		}
		
		Class<S> domainType = resolveDomainType();
		DefaultJPAService<S> target = new DefaultJPAService<S>();
		target.setEntityClass(domainType);
		target.setJPAManagerFactory(jpaManagerFactory);
		target.afterPropertiesSet();

		ProxyFactory result = new ProxyFactory();
		result.setTarget(target);
		result.setInterfaces(new Class[] { serviceInterface, JPAService.class });
		
		return (T) result.getProxy(classLoader);
	}

	@SuppressWarnings("unchecked")
	private Class<S> resolveDomainType() {

		Class<?>[] arguments = resolveTypeArguments(serviceInterface, JPAService.class);

		if (arguments == null || arguments[0] == null) {
			throw new IllegalArgumentException(String.format("Could not resolve domain type of %s!", serviceInterface));
		}

		return (Class<S>) arguments[0];
	}
}
