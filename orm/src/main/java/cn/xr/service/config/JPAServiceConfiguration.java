package cn.xr.service.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class JPAServiceConfiguration {

	private static final String DEFAULT_SERVICE_IMPLEMENTATION_POSTFIX = "Impl";

	private final JPAServiceConfigurationSource configurationSource;
	private final String interfaceName;

	/**
	 * Creates a new {@link JPAServiceConfiguration} from the given {@link JPAServiceConfigurationSource} and
	 * interface name.
	 * 
	 * @param configurationSource must not be {@literal null}.
	 * @param interfaceName must not be {@literal null} or empty.
	 */
	public JPAServiceConfiguration(JPAServiceConfigurationSource configurationSource, String interfaceName) {

		Assert.notNull(configurationSource);
		Assert.hasText(interfaceName);

		this.configurationSource = configurationSource;
		this.interfaceName = interfaceName;
	}

	/**
	 * Returns the base packages that the repository was scanned under.
	 * 
	 * @return
	 */
	Iterable<String> getBasePackages() {
		return configurationSource.getBasePackages();
	}

	/**
	 * Returns the interface name of the repository.
	 * 
	 * @return
	 */
	String getServiceInterface() {
		return interfaceName;
	}

	/**
	 * Returns the class name of the custom implementation.
	 * 
	 * @return
	 */
	String getImplementationClassName() {
		return ClassUtils.getShortName(interfaceName) + getImplementationPostfix();
	}

	/**
	 * Returns the bean name of the custom implementation.
	 * 
	 * @return
	 */
	public String getImplementationBeanName() {
		return StringUtils.uncapitalize(getImplementationClassName());
	}

	/**
	 * Returns the configured postfix to be used for looking up custom implementation classes.
	 * 
	 * @return the postfix to use or {@literal null} in case none is configured.
	 */
	String getImplementationPostfix() {
		String configuredPostfix = configurationSource.getServiceImplementationPostfix();
		return StringUtils.hasText(configuredPostfix) ? configuredPostfix : DEFAULT_SERVICE_IMPLEMENTATION_POSTFIX;
	}

	/**
	 * Returns the name of the {@link FactoryBean} class to be used to create repository instances.
	 * 
	 * @return
	 */
	public String getJPAServiceFactoryBeanName() {
		return configurationSource.getServiceFactoryBeanName();
	}

	/**
	 * Returns the source of the {@link RepositoryConfiguration}.
	 * 
	 * @return
	 */
	public Object getSource() {
		return configurationSource.getSource();
	}

	/**
	 * Returns the {@link RepositoryConfigurationSource} that backs the {@link RepositoryConfiguration}.
	 * 
	 * @return
	 */
	public JPAServiceConfigurationSource getConfigurationSource() {
		return configurationSource;
	}

}
