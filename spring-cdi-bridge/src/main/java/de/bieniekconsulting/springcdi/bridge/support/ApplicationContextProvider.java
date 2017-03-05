package de.bieniekconsulting.springcdi.bridge.support;

import org.springframework.context.ConfigurableApplicationContext;

public interface ApplicationContextProvider {
	ConfigurableApplicationContext provideContext();
}
