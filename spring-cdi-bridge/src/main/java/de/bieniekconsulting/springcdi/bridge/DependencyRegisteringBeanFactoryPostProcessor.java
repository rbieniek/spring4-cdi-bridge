package de.bieniekconsulting.springcdi.bridge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import de.bieniekconsulting.springcdi.bridge.api.Cdi;

public class DependencyRegisteringBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	private final BeanManager beanManager;
	private final List<Bean<Object>> cdiBeans;

	public DependencyRegisteringBeanFactoryPostProcessor(final BeanManager beanManager,
			final List<Bean<Object>> cdiBeans) {
		this.beanManager = beanManager;
		this.cdiBeans = cdiBeans;
	}

	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
		final BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
		final Map<String, Bean<Object>> beans = new HashMap<>();

		for (final Bean<Object> bean : cdiBeans) {
			final BeanDefinition beanDefinition = createBeanDefinition(bean);
			final String beanName = createBeanName(bean, beanDefinition, registry);
			registry.registerBeanDefinition(beanName, beanDefinition);
			beans.put(beanName, bean);
		}

		final CdiScope cdiScope = new CdiScope(beanManager, beans);

		beanFactory.registerScope(Cdi.SCOPE_CDI, cdiScope);
	}

	private BeanDefinition createBeanDefinition(final Bean<Object> bean) {
		final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();

		beanDefinition.setBeanClass(bean.getBeanClass());
		beanDefinition.setScope(Cdi.SCOPE_CDI);
		beanDefinition.setLazyInit(true);

		return beanDefinition;
	}

	private String createBeanName(final Bean<Object> bean, final BeanDefinition beanDefinition,
			final BeanDefinitionRegistry registry) {
		String beanName = bean.getName();

		if (beanName == null) {
			beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, registry);
		}

		return beanName;
	}
}
