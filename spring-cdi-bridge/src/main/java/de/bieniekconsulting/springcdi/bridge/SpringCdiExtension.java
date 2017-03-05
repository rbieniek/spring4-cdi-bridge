package de.bieniekconsulting.springcdi.bridge;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.type.StandardMethodMetadata;

import de.bieniekconsulting.springcdi.bridge.support.ApplicationContextProvider;

public class SpringCdiExtension implements Extension {
	private List<Bean<Object>> cdiBeans = new LinkedList<>();

	public void addBean(@Observes final ProcessBean<Object> bean) {
		cdiBeans.add(bean.getBean());
	}

	public void connectCdiAndSpring(@Observes final AfterBeanDiscovery event, final BeanManager manager)
			throws ClassNotFoundException {
		final List<ConfigurableApplicationContext> contexts = applicationContextFromServiceLoaders();

		for (final ConfigurableApplicationContext context : contexts) {

			if (!context.isActive()) {
				context.addBeanFactoryPostProcessor(
						new DependencyRegisteringBeanFactoryPostProcessor(manager, cdiBeans));

				context.refresh();
			}

			for (final String beanName : context.getBeanDefinitionNames()) {
				final BeanDefinition beanDefinition = context.getBeanFactory().getBeanDefinition(beanName);

				if (!CdiScope.class.getName().equals(beanDefinition.getScope())) {
					createBean(beanName, beanDefinition, context.getBeanFactory(), manager)
							.ifPresent(bean -> event.addBean(bean));
				}
			}

		}
	}

	private Optional<Bean<?>> createBean(final String beanName, final BeanDefinition beanDefinition,
			final ConfigurableBeanFactory beanFactory, final BeanManager beanManager) throws ClassNotFoundException {
		final Optional<Class<?>> beanClass = determineBeanClass(beanDefinition);

		if (!beanClass.isPresent()) {
			return Optional.empty();
		}

		final AnnotatedType<?> annotatedType = beanManager.createAnnotatedType(beanClass.get());
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
		return Optional.of(new SpringBean(beanName, beanClass.get(), beanTypes, qualifiers, stereotypes, beanFactory));
	}

	private Optional<Class<?>> determineBeanClass(final BeanDefinition beanDefinition) {
		if (beanDefinition instanceof RootBeanDefinition) {
			final Class<?> targetType = ((RootBeanDefinition) beanDefinition).getTargetType();

			if (targetType != null) {
				return Optional.of(targetType);
			}
		}

		if (beanDefinition.getSource() instanceof StandardMethodMetadata) {
			final StandardMethodMetadata source = (StandardMethodMetadata) beanDefinition.getSource();

			try {
				return Optional.of(Class.forName(source.getReturnTypeName()));
			} catch (final Exception e) {
				return Optional.empty();
			}

		}

		if (StringUtils.isNotBlank(beanDefinition.getBeanClassName())) {
			try {
				return Optional.of(Class.forName(beanDefinition.getBeanClassName()));
			} catch (final Exception e) {
				return Optional.empty();
			}
		}

		return Optional.empty();
	}

	/**
	 * Obtain application context instances from service provider. Allows mixed
	 * scenarios where some shared application context must be used for bean
	 * manager and spring servlet initialization
	 *
	 * @return
	 */
	private List<ConfigurableApplicationContext> applicationContextFromServiceLoaders() {
		final List<ConfigurableApplicationContext> contexts = new LinkedList<>();

		final ServiceLoader<ApplicationContextProvider> serviceLoader = ServiceLoader
				.load(ApplicationContextProvider.class);

		serviceLoader.iterator().forEachRemaining(provider -> contexts.add(provider.provideContext()));

		return contexts;
	}

}
