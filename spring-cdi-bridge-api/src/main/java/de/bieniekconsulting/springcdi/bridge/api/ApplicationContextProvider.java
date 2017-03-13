package de.bieniekconsulting.springcdi.bridge.api;

import org.springframework.context.ConfigurableApplicationContext;

public interface ApplicationContextProvider {
	ConfigurableApplicationContext provideContext();

	default boolean closeOnShutdown() {
		return false;
	}
}