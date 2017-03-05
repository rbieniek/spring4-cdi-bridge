package de.bieniekconsulting.springcdi.bridge;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.util.AnnotationLiteral;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class SpringCdiExtension implements Extension {
	private List<Bean<Object>> cdiBeans = new LinkedList<>();

	public void addBean(@Observes final ProcessBean<Object> bean) {
		cdiBeans.add(bean.getBean());
	}

	public void connectCdiAndSpring(@Observes final AfterBeanDiscovery event, final BeanManager manager)
			throws ClassNotFoundException {
		final List<Pair<ApplicationContextProvider, ConfigurableApplicationContext>> contexts = applicationContextFromServiceLoaders();

		for (final Pair<ApplicationContextProvider, ConfigurableApplicationContext> contextPair : contexts) {
			final ConfigurableApplicationContext context = contextPair.getRight();
			final GenericApplicationContext cdiEnhancedScope = new GenericApplicationContext(context);

			cdiEnhancedScope
					.addBeanFactoryPostProcessor(new DependencyRegisteringBeanFactoryPostProcessor(manager, cdiBeans));

			cdiEnhancedScope.refresh();

			contextPair.getLeft().cdiEnhancedContext(cdiEnhancedScope);

			for (final String beanName : context.getBeanDefinitionNames()) {
				final BeanDefinition beanDefinition = context.getBeanFactory().getBeanDefinition(beanName);

				if (!CdiScope.class.getName().equals(beanDefinition.getScope())) {
					event.addBean(createBean(beanName, beanDefinition, context.getBeanFactory(), manager));
				}
			}

		}
	}

	private Bean<?> createBean(final String beanName, final BeanDefinition beanDefinition,
			final ConfigurableBeanFactory beanFactory, final BeanManager beanManager) throws ClassNotFoundException {
		final Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
		final AnnotatedType<?> annotatedType = beanManager.createAnnotatedType(beanClass);
		final Set<Type> beanTypes = annotatedType.getTypeClosure();
		final Set<Annotation> qualifiers = new HashSet<>();

		qualifiers.add(new AnnotationLiteral<Any>() {
		});
		qualifiers.add(new AnnotationLiteral<Default>() {
		});

		final Set<Class<? extends Annotation>> stereotypes = new HashSet<>();

		for (final Annotation annotation : annotatedType.getAnnotations()) {
			if (beanManager.isQualifier(annotation.annotationType())) {
				qualifiers.add(annotation);
			}
			if (beanManager.isStereotype(annotation.annotationType())) {
				stereotypes.add(annotation.annotationType());
			}
		}
		return new SpringBean(beanName, beanClass, beanTypes, qualifiers, stereotypes, beanFactory);
	}

	/**
	 * Obtain application context instances from service provider. Allows mixed
	 * scenarios where some shared application context must be used for bean
	 * manager and spring servlet initialization
	 *
	 * @return
	 */
	private List<Pair<ApplicationContextProvider, ConfigurableApplicationContext>> applicationContextFromServiceLoaders() {
		final List<Pair<ApplicationContextProvider, ConfigurableApplicationContext>> contexts = new LinkedList<>();

		final ServiceLoader<ApplicationContextProvider> serviceLoader = ServiceLoader
				.load(ApplicationContextProvider.class);

		serviceLoader.iterator()
				.forEachRemaining(provider -> contexts.add(Pair.of(provider, provider.provideContext())));

		return contexts;
	}

}
