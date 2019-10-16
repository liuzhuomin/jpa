package cn.xr.service.config;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JPAServiceConfigurationSource {

	private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";
	private static final String EXCLUDE_FILTER_ELEMENT = "exclude-filter";
	private static final String INCLUDE_FILTER_ELEMENT = "include-filter";
	private static final String FILTER_TYPE_ATTRIBUTE = "type";
	private static final String FILTER_EXPRESSION_ATTRIBUTE = "expression";
	private static final String SERVICE_IMPL_POSTFIX = "service-impl-postfix";
	private static final String SERVICE_FACTORY_BEAN_CLASS_NAME = "factory-class";

	private final Environment environment;

	private final Element element;
	private final ParserContext context;

	private final Iterable<TypeFilter> includeFilters;
	private final Iterable<TypeFilter> excludeFilters;

	public JPAServiceConfigurationSource(Element element, ParserContext context, Environment environment) {
		this.element = element;
		this.context = context;
		this.environment = environment;
		this.includeFilters = parseTypeFilters(INCLUDE_FILTER_ELEMENT);
		this.excludeFilters = parseTypeFilters(EXCLUDE_FILTER_ELEMENT);
	}

	public Object getSource() {
		return context.extractSource(element);
	}

	public Iterable<String> getBasePackages() {
		String attribute = element.getAttribute(BASE_PACKAGE_ATTRIBUTE);
		return Arrays.asList(StringUtils.delimitedListToStringArray(attribute, ",", " "));
	}

	protected Iterable<TypeFilter> getIncludeFilters() {
		return includeFilters;
	}

	protected Iterable<TypeFilter> getExcludeFilters() {
		return excludeFilters;
	}

	public Element getElement() {
		return element;
	}

	public String getServiceImplementationPostfix() {
		return getNullDefaultedAttribute(element, SERVICE_IMPL_POSTFIX);
	}

	public String getServiceFactoryBeanName() {
		return getNullDefaultedAttribute(element, SERVICE_FACTORY_BEAN_CLASS_NAME);
	}

	public Collection<JPAServiceConfiguration> getCandidates(ResourceLoader loader) {
		JPAServiceComponentProvider scanner = new JPAServiceComponentProvider(getIncludeFilters());
		scanner.setResourceLoader(loader);
		scanner.setEnvironment(environment);

		for (TypeFilter filter : getExcludeFilters()) {
			scanner.addExcludeFilter(filter);
		}

		Set<JPAServiceConfiguration> result = new HashSet<JPAServiceConfiguration>();

		for (String basePackage : getBasePackages()) {
			Collection<BeanDefinition> components = scanner.findCandidateComponents(basePackage);
			for (BeanDefinition definition : components) {
				result.add(new JPAServiceConfiguration(this, definition.getBeanClassName()));
			}
		}

		return result;
	}

	protected Iterable<TypeFilter> parseTypeFilters(String filterType) {

		Collection<TypeFilter> filters = new HashSet<TypeFilter>();
		
		XmlReaderContext readerContext = context.getReaderContext();
		ClassLoader classLoader = readerContext.getResourceLoader().getClassLoader();
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String localName = context.getDelegate().getLocalName(node);
				try {
					if (filterType.equals(localName)) {
						filters.add(createTypeFilter((Element) node, classLoader));
					}
				}
				catch (Exception ex) {
					readerContext.error(ex.getMessage(), readerContext.extractSource(element), ex.getCause());
				}
			}
		}
		
		return filters;
	}

	@SuppressWarnings("unchecked")
	protected TypeFilter createTypeFilter(Element element, ClassLoader classLoader) {
		String filterType = element.getAttribute(FILTER_TYPE_ATTRIBUTE);
		String expression = element.getAttribute(FILTER_EXPRESSION_ATTRIBUTE);
		try {
			if ("annotation".equals(filterType)) {
				return new AnnotationTypeFilter((Class<Annotation>) classLoader.loadClass(expression));
			}
			else if ("assignable".equals(filterType)) {
				return new AssignableTypeFilter(classLoader.loadClass(expression));
			}
			else if ("aspectj".equals(filterType)) {
				return new AspectJTypeFilter(expression, classLoader);
			}
			else if ("regex".equals(filterType)) {
				return new RegexPatternTypeFilter(Pattern.compile(expression));
			}
			else if ("custom".equals(filterType)) {
				Class<?> filterClass = classLoader.loadClass(expression);
				if (!TypeFilter.class.isAssignableFrom(filterClass)) {
					throw new IllegalArgumentException(
							"Class is not assignable to [" + TypeFilter.class.getName() + "]: " + expression);
				}
				return (TypeFilter) BeanUtils.instantiateClass(filterClass);
			}
			else {
				throw new IllegalArgumentException("Unsupported filter type: " + filterType);
			}
		}
		catch (ClassNotFoundException ex) {
			throw new FatalBeanException("Type filter class not found: " + expression, ex);
		}
	}

	private String getNullDefaultedAttribute(Element element, String attributeName) {
		String attribute = element.getAttribute(attributeName);
		return StringUtils.hasText(attribute) ? attribute : null;
	}
}
