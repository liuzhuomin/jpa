package cn.xr.service.config;

import cn.xr.service.JPAService;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JPAServiceComponentProvider extends ClassPathScanningCandidateComponentProvider {

	public JPAServiceComponentProvider(Iterable<? extends TypeFilter> includeFilters) {
		super(false);

		Assert.notNull(includeFilters);

		if (includeFilters.iterator().hasNext()) {
			for (TypeFilter filter : includeFilters) {
				addIncludeFilter(filter);
			}
		} else {
			super.addIncludeFilter(new InterfaceTypeFilter(JPAService.class));
		}
	}

	/**
	 * Custom extension of {@link #addIncludeFilter(TypeFilter)} to extend the added {@link TypeFilter}. For the
	 * {@link TypeFilter} handed we'll have two filters registered: one additionally enforcing the
	 * {@link RepositoryDefinition} annotation, the other one forcing the extension of {@link Repository}.
	 * 
	 * @see ClassPathScanningCandidateComponentProvider#addIncludeFilter(TypeFilter)
	 */
	@Override
	public void addIncludeFilter(TypeFilter includeFilter) {

		List<TypeFilter> filterPlusInterface = new ArrayList<TypeFilter>(2);
		filterPlusInterface.add(includeFilter);
		filterPlusInterface.add(new InterfaceTypeFilter(JPAService.class));

		super.addIncludeFilter(new AllTypeFilter(filterPlusInterface));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#isCandidateComponent(org.springframework.beans.factory.annotation.AnnotatedBeanDefinition)
	 */
	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {

		boolean isNonServiceInterface = !JPAService.class.getName().equals(beanDefinition.getBeanClassName());
		boolean isTopLevelType = !beanDefinition.getMetadata().hasEnclosingClass();

		return isNonServiceInterface && isTopLevelType;
	}

	/**
	 * {@link TypeFilter} that only matches interfaces. Thus setting this up makes
	 * only sense providing an interface type as {@code targetType}.
	 * 
	 * @author Oliver Gierke
	 */
	private static class InterfaceTypeFilter extends AssignableTypeFilter {

		/**
		 * Creates a new {@link InterfaceTypeFilter}.
		 * 
		 * @param targetType
		 */
		public InterfaceTypeFilter(Class<?> targetType) {
			super(targetType);
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter#match(org.springframework.core.type.classreading.MetadataReader, org.springframework.core.type.classreading.MetadataReaderFactory)
		 */
		@Override
		public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {

			return metadataReader.getClassMetadata().isInterface() && super.match(metadataReader, metadataReaderFactory);
		}
	}

	/**
	 * Helper class to create a {@link TypeFilter} that matches if all the delegates match.
	 * 
	 * @author Oliver Gierke
	 */
	private static class AllTypeFilter implements TypeFilter {

		private final List<TypeFilter> delegates;

		/**
		 * Creates a new {@link AllTypeFilter} to match if all the given delegates match.
		 * 
		 * @param delegates must not be {@literal null}.
		 */
		public AllTypeFilter(List<TypeFilter> delegates) {

			Assert.notNull(delegates);
			this.delegates = delegates;
		}

		/* 
		 * (non-Javadoc)
		 * @see org.springframework.core.type.filter.TypeFilter#match(org.springframework.core.type.classreading.MetadataReader, org.springframework.core.type.classreading.MetadataReaderFactory)
		 */
		@Override
		public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {

			for (TypeFilter filter : delegates) {
				if (!filter.match(metadataReader, metadataReaderFactory)) {
					return false;
				}
			}

			return true;
		}
	}
}
