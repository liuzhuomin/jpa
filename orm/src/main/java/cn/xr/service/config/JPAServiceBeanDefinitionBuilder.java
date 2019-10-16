package cn.xr.service.config;

import cn.xr.service.support.JPAServiceFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class JPAServiceBeanDefinitionBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPAServiceBeanDefinitionBuilder.class);

    private final JPAServiceConfiguration configuration;

    /**
     * Creates a new {@link JPAServiceBeanDefinitionBuilder} from the given {@link JPAServiceConfiguration}.
     *
     * @param configuration must not be {@literal null}.
     */
    public JPAServiceBeanDefinitionBuilder(JPAServiceConfiguration configuration) {
        Assert.notNull(configuration);
        this.configuration = configuration;
    }

    public String getFactoryBeanName() {
        String factoryBeanName = configuration.getJPAServiceFactoryBeanName();
        return StringUtils.hasText(factoryBeanName) ? factoryBeanName : JPAServiceFactoryBean.class.getName();
    }

    /**
     * Builds a new {@link BeanDefinitionBuilder} from the given {@link BeanDefinitionRegistry} and {@link ResourceLoader}
     * .
     *
     * @param registry       must not be {@literal null}.
     * @param resourceLoader must not be {@literal null}.
     * @return
     */
    public BeanDefinitionBuilder build(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {

        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");

        String factoryBeanName = getFactoryBeanName();

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(factoryBeanName);

        builder.getRawBeanDefinition().setSource(configuration.getSource());
        builder.addPropertyValue("serviceInterface", configuration.getServiceInterface());

        String customImplementationBeanName = registerCustomImplementation(registry, resourceLoader);

        if (customImplementationBeanName != null) {
            builder.addPropertyReference("customImplementation", customImplementationBeanName);
            builder.addDependsOn(customImplementationBeanName);
        }

        return builder;
    }

    private String registerCustomImplementation(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {

        String beanName = configuration.getImplementationBeanName();

        // Already a bean configured?
        if (registry.containsBeanDefinition(beanName)) {
            return beanName;
        }

        AbstractBeanDefinition beanDefinition = detectCustomImplementation(registry, resourceLoader);

        if (null == beanDefinition) {
            return null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Registering custom repository implementation: " + configuration.getImplementationBeanName() + " "
                    + beanDefinition.getBeanClassName());
        }

        beanDefinition.setAutowireCandidate(false);
        beanDefinition.setSource(configuration.getSource());

        registry.registerBeanDefinition(beanName, beanDefinition);

        return beanName;
    }

    /**
     * Tries to detect a custom implementation for a JPAService bean by classpath scanning.
     *
     * @param config
     * @param parser
     * @return the {@code AbstractBeanDefinition} of the custom implementation or {@literal null} if none found
     */
    private AbstractBeanDefinition detectCustomImplementation(BeanDefinitionRegistry registry, ResourceLoader loader) {

        // Build pattern to lookup implementation class
        Pattern pattern = Pattern.compile(".*\\." + configuration.getImplementationClassName());

        // Build classpath scanner and lookup bean definition
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.setResourceLoader(loader);
        provider.addIncludeFilter(new RegexPatternTypeFilter(pattern));

        Set<BeanDefinition> definitions = new HashSet<BeanDefinition>();

        for (String basePackage : configuration.getBasePackages()) {
            definitions.addAll(provider.findCandidateComponents(basePackage));
        }

        if (definitions.isEmpty()) {
            return null;
        }

        if (definitions.size() == 1) {
            return (AbstractBeanDefinition) definitions.iterator().next();
        }

        List<String> implementationClassNames = new ArrayList<String>();
        for (BeanDefinition bean : definitions) {
            implementationClassNames.add(bean.getBeanClassName());
        }

        throw new IllegalStateException(String.format(
                "Ambiguous custom implementations detected! Found %s but expected a single implementation!",
                StringUtils.collectionToCommaDelimitedString(implementationClassNames)));
    }
}
