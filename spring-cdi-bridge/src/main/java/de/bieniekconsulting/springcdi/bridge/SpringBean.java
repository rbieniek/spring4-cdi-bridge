package de.bieniekconsulting.springcdi.bridge;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;

public class SpringBean implements Bean<Object> {

	private String beanName;
	private Class<?> beanClass;
	private Set<Type> beanTypes;
	private Set<Annotation> qualifiers;
	private Set<Class<? extends Annotation>> stereotypes;
	private ConfigurableBeanFactory beanFactory;

	public SpringBean(final String beanName, final Class<?> beanClass, final Set<Type> beanTypes,
			final Set<Annotation> qualifiers, final Set<Class<? extends Annotation>> stereotypes,
			final ConfigurableBeanFactory beanFactory) {
		this.beanName = beanName;
		this.beanClass = beanClass;
		this.beanTypes = Collections.unmodifiableSet(beanTypes);
		this.qualifiers = Collections.unmodifiableSet(qualifiers);
		this.stereotypes = Collections.unmodifiableSet(stereotypes);
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
		return Dependent.class;
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
