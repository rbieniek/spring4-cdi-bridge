package de.bieniekconsulting.springcdi.bridge;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;

public class SpringBean implements Bean<Object> {

	private final String beanName;
	private final Class<?> beanClass;
	private final Set<Type> beanTypes;
	private final Set<Annotation> qualifiers;
	private final Set<Class<? extends Annotation>> stereotypes;
	private final ConfigurableBeanFactory beanFactory;
	private final Class<? extends Annotation> scope;

	public SpringBean(final String beanName, final Class<?> beanClass, final Set<Type> beanTypes,
			final Set<Annotation> qualifiers, final Set<Class<? extends Annotation>> stereotypes,
			final Class<? extends Annotation> scope, final ConfigurableBeanFactory beanFactory) {
		this.beanName = beanName;
		this.beanClass = beanClass;
		this.beanTypes = Collections.unmodifiableSet(beanTypes);
		this.qualifiers = Collections.unmodifiableSet(qualifiers);
		this.stereotypes = Collections.unmodifiableSet(stereotypes);
		this.scope = scope;
		this.beanFactory = beanFactory;
	}

	@Override
	public String getName() {
		return beanName;
	}

	@Override
	public Class<?> getBeanClass() {
		return beanClass;
	}

	@Override
	public Set<Type> getTypes() {
		return beanTypes;
	}

	@Override
	public Set<Annotation> getQualifiers() {
		return qualifiers;
	}

	@Override
	public Set<Class<? extends Annotation>> getStereotypes() {
		return stereotypes;
	}

	@Override
	public Class<? extends Annotation> getScope() {
		return scope;
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		return Collections.emptySet();
	}

	@Override
	public boolean isAlternative() {
		return false;
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	@Override
	public Object create(final CreationalContext<Object> creationalContext) {
		return beanFactory.getBean(beanName);
	}

	@Override
	public void destroy(final Object beanInstance, final CreationalContext<Object> creationalContext) {
		beanFactory.destroyBean(beanName, beanInstance);
	}
}
