package de.bieniekconsulting.springcdi.bridge;

import java.util.Map;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class CdiScope implements Scope {

	private BeanManager beanManager;
	private Map<String, Bean<Object>> beans;

	public CdiScope(final BeanManager beanManager, final Map<String, Bean<Object>> beans) {
		this.beanManager = beanManager;
		this.beans = beans;
	}

	@Override
	public Object get(final String name, final ObjectFactory<?> objectFactory) {
		final Bean<Object> bean = beans.get(name);
		final CreationalContext<Object> context = beanManager.createCreationalContext(bean);

		return beanManager.getReference(bean, bean.getBeanClass(), context);
	}

	@Override
	public String getConversationId() {
		// Intentionally left blank
		return null;
	}

	@Override
	public void registerDestructionCallback(final String name, final Runnable callback) {
		// Intentionally left blank

	}

	@Override
	public Object remove(final String name) {
		final Object beanInstance = get(name, null);
		final Bean<Object> bean = beans.get(name);
		final CreationalContext<Object> context = beanManager.createCreationalContext(bean);

		bean.destroy(beanInstance, context);

		return beanInstance;
	}

	@Override
	public Object resolveContextualObject(final String key) {
		// Intentionally left blank
		return null;
	}

}
