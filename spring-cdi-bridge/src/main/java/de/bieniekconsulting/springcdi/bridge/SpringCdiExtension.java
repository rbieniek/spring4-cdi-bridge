package de.bieniekconsulting.springcdi.bridge;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.util.AnnotationLiteral;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.MethodMetadata;

import de.bieniekconsulting.springcdi.bridge.api.ApplicationContextProvider;
import de.bieniekconsulting.springcdi.bridge.api.SpringScoped;

public class SpringCdiExtension implements Extension {
	private static final Logger logger = LoggerFactory.getLogger(SpringCdiExtension.class.getName());

	private final List<Bean<Object>> cdiBeans = new LinkedList<>();
	private final List<ConfigurableApplicationContext> contexts = new LinkedList<>();

	public void addBean(@Observes final ProcessBean<Object> bean) {
		cdiBeans.add(bean.getBean());
	}

	public void connectCdiAndSpring(@Observes final AfterBeanDiscovery event, final BeanManager manager)
			throws ClassNotFoundException {
		logger.info("Initializing Spring CDI bridge");

		final List<Pair<ConfigurableApplicationContext, Boolean>> contextPairs = applicationContextFromServiceLoaders();

		for (final Pair<ConfigurableApplicationContext, Boolean> contextPair : contextPairs) {
			final ConfigurableApplicationContext context = contextPair.getLeft();

			if (!context.isActive()) {
				context.addBeanFactoryPostProcessor(
						new DependencyRegisteringBeanFactoryPostProcessor(manager, cdiBeans));

				context.refresh();
			}

			if (contextPair.getRight()) {
				this.contexts.add(context);
			}

			for (final String beanName : context.getBeanDefinitionNames()) {
				final BeanDefinition beanDefinition = context.getBeanFactory().getBeanDefinition(beanName);

				if (!CdiScope.class.getName().equals(beanDefinition.getScope())) {
					logger.info("attempting to add spring bean {} to CDI container", beanName);

					createBean(beanName, beanDefinition, context.getBeanFactory(), manager)
							.ifPresent(bean -> event.addBean(bean));
				}
			}

		}
	}

	public void closeSpringContexts(@Observes final BeforeShutdown event) {
		logger.info("stopping application contexts");

		for (final ConfigurableApplicationContext context : contexts) {
			try {
				context.stop();
			} catch (final Exception e) {
				logger.info("cannot close spring application context", e);
			}
		}
	}

	@SuppressWarnings("serial")
	private Optional<Bean<?>> createBean(final String beanName, final BeanDefinition beanDefinition,
			final ConfigurableBeanFactory beanFactory, final BeanManager beanManager) throws ClassNotFoundException {

		if (!isSpringScoped(beanDefinition)) {
			logger.info("spring bean {} is not spring scoped, skippig", beanName);

			return Optional.empty();
		}

		final Optional<Class<?>> beanClass = determineBeanClass(beanDefinition);

		if (!beanClass.isPresent()) {
			logger.info("cannot determine class for spring bean {}, skippig", beanName);

			return Optional.empty();
		}

		logger.info("determined class {} for spring bean {}", beanClass.get().getName(), beanName);

		final AnnotatedType<?> annotatedType = beanManager.createAnnotatedType(beanClass.get());
		final Set<Type> beanTypes = annotatedType.getTypeClosure();
		final Set<Annotation> qualifiers = new HashSet<>();

		qualifiers.add(new AnnotationLiteral<Any>() {
		});
		qualifiers.add(new AnnotationLiteral<Default>() {
		});

		final Class<? extends Annotation> scope;

		if (beanDefinition.isSingleton()) {
			scope = ApplicationScoped.class;
		} else {
			scope = Dependent.class;
		}

		final Set<Class<? extends Annotation>> stereotypes = new HashSet<>();

		for (final Annotation annotation : annotatedType.getAnnotations()) {
			if (beanManager.isQualifier(annotation.annotationType())) {
				qualifiers.add(annotation);
			}
			if (beanManager.isStereotype(annotation.annotationType())) {
				stereotypes.add(annotation.annotationType());
			}
		}
		return Optional
				.of(new SpringBean(beanName, beanClass.get(), beanTypes, qualifiers, stereotypes, scope, beanFactory));
	}

	private boolean isSpringScoped(final BeanDefinition beanDefinition) {
		if (beanDefinition.getSource() instanceof AnnotatedTypeMetadata) {
			return ((AnnotatedTypeMetadata) beanDefinition.getSource()).isAnnotated(SpringScoped.class.getName());
		}
		if (beanDefinition instanceof AnnotatedBeanDefinition) {
			return ((AnnotatedBeanDefinition) beanDefinition).getMetadata().isAnnotated(SpringScoped.class.getName());
		}
		return false;
	}

	private Optional<Class<?>> determineBeanClass(final BeanDefinition beanDefinition) {
		if (beanDefinition instanceof RootBeanDefinition) {
			final Class<?> targetType = ((RootBeanDefinition) beanDefinition).getTargetType();

			if (targetType != null) {
				return Optional.of(targetType);
			}
		}

		if (beanDefinition.getSource() instanceof MethodMetadata) {
			final MethodMetadata source = (MethodMetadata) beanDefinition.getSource();

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
	private List<Pair<ConfigurableApplicationContext, Boolean>> applicationContextFromServiceLoaders() {
		final List<Pair<ConfigurableApplicationContext, Boolean>> contexts = new LinkedList<>();

		final ServiceLoader<ApplicationContextProvider> serviceLoader = ServiceLoader
				.load(ApplicationContextProvider.class);

		serviceLoader.iterator().forEachRemaining(provider -> {
			logger.info("found application context provider class {}", provider.getClass().getName());

			contexts.add(Pair.of(provider.provideContext(), provider.closeOnShutdown()));
		});

		return contexts;
	}

}
