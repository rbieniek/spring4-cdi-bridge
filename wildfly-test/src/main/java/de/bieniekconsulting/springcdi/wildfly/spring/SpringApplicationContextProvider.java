package de.bieniekconsulting.springcdi.wildfly.spring;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bieniekconsulting.springcdi.bridge.support.ApplicationContextProvider;

public class SpringApplicationContextProvider implements ApplicationContextProvider {

	@Override
	public ConfigurableApplicationContext provideContext() {
		return new AnnotationConfigApplicationContext(SpringGreeterContext.class);
	}

}
