package cn.xr.service.config;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.ClassUtils;

public class JPAServiceBeanNameGenerator implements BeanNameGenerator, BeanClassLoaderAware {

	private static final BeanNameGenerator DELEGATE = new AnnotationBeanNameGenerator();

	private ClassLoader beanClassLoader;

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang.ClassLoader)
	 */
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.support.BeanNameGenerator#generateBeanName(org.springframework.beans.factory.config.BeanDefinition, org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 */
	@Override
	public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {

		AnnotatedBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(getServiceInterfaceFrom(definition));
		return DELEGATE.generateBeanName(beanDefinition, registry);
	}

	/**
	 * Returns the type configured for the {@code serviceInterface} property of the given bean definition. Uses a
	 * potential {@link Class} being configured as is or tries to load a class with the given value's {@link #toString()}
	 * representation.
	 * 
	 * @param beanDefinition
	 * @return
	 */
	private Class<?> getServiceInterfaceFrom(BeanDefinition beanDefinition) {

		Object value = beanDefinition.getPropertyValues().getPropertyValue("serviceInterface").getValue();

		if (value instanceof Class<?>) {
			return (Class<?>) value;
		} else {
			try {
				return ClassUtils.forName(value.toString(), beanClassLoader);
			} catch (Exception o_O) {
				throw new RuntimeException(o_O);
			}
		}
	}
}
