package de.bieniekconsulting.springcdi.wildfly.spring.dbms.beans;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bieniekconsulting.springcdi.bridge.api.ApplicationContextProvider;

public class JdbcSpringApplicationContextProvider implements ApplicationContextProvider {

	@Override
	public ConfigurableApplicationContext provideContext() {
		return new AnnotationConfigApplicationContext(JdbcContext.class);
	}

	@Override
	public boolean closeOnShutdown() {
		return true;
	}

}
