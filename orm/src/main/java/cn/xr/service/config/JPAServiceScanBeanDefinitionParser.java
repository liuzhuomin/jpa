package cn.xr.service.config;

import cn.xr.service.support.DefaultJPAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ReaderContext;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.env.Environment;
import org.w3c.dom.Element;

public class JPAServiceScanBeanDefinitionParser implements BeanDefinitionParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPAServiceScanBeanDefinitionParser.class);

    @Override
    public BeanDefinition parse(Element element, ParserContext parser) {
        try {
            BeanDefinitionParserDelegate delegate = parser.getDelegate();
            System.out.println(789);
            System.err.println("through here???????????????????????????????????????"+delegate);
            //TODO 需要处理这个函数不存在delegate.getEnvironment();
//            Environment environment = delegate.getEnvironment();
            JPAServiceConfigurationSource configSource = new JPAServiceConfigurationSource(element, parser, null);

            for (JPAServiceConfiguration configuration : configSource.getCandidates(parser.getReaderContext().getResourceLoader())) {
                registerGenericServiceFactoryBean(configuration, parser);
            }
        } catch (RuntimeException e) {
            handleError(e, element, parser.getReaderContext());
        }

        return null;
    }

    protected void registerGenericServiceFactoryBean(JPAServiceConfiguration configuration, ParserContext parser) {
        JPAServiceBeanDefinitionBuilder definitionBuilder = new JPAServiceBeanDefinitionBuilder(configuration);

        try {
            BeanDefinitionBuilder builder = definitionBuilder.build(parser.getRegistry(), parser.getReaderContext().getResourceLoader());
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
            beanDefinition.setSource(configuration.getSource());

            JPAServiceBeanNameGenerator generator = new JPAServiceBeanNameGenerator();
            generator.setBeanClassLoader(parser.getReaderContext().getBeanClassLoader());

            String beanName = generator.generateBeanName(beanDefinition, parser.getRegistry());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Registering JPAService: " + beanName + " - Interface: " + configuration.getServiceInterface()
                        + " - Factory: " + definitionBuilder.getFactoryBeanName());
            }

            BeanComponentDefinition definition = new BeanComponentDefinition(beanDefinition, beanName);
            parser.registerBeanComponent(definition);

            registerOSGiService(parser, beanName, configuration.getServiceInterface());
        } catch (RuntimeException e) {
            handleError(e, configuration.getConfigurationSource().getElement(), parser.getReaderContext());
        }

    }

    protected void registerOSGiService(ParserContext parser, String beanName, String interfaceName) {
        String exportBeanName = getOSGiExportBeanName(beanName);
        BeanDefinition exportBeanDefinition = buildExportBean(beanName, interfaceName);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Registered OSGi service for JPAService: " + beanName);
        }
        BeanComponentDefinition definition = new BeanComponentDefinition(exportBeanDefinition, exportBeanName);
        parser.registerBeanComponent(definition);
    }

    private BeanDefinition buildExportBean(String beanName, String interfaceName) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DefaultJPAService.class);
        builder.addPropertyValue("targetBeanName", beanName);
        builder.addPropertyValue("interfaces", interfaceName);
        return builder.getBeanDefinition();
    }

    private String getOSGiExportBeanName(String beanName) {
        return DefaultJPAService.class.getName() + "#" + beanName;
    }

    private void handleError(Exception e, Element source, ReaderContext reader) {
        reader.error(e.getMessage(), reader.extractSource(source), e);
    }
}
