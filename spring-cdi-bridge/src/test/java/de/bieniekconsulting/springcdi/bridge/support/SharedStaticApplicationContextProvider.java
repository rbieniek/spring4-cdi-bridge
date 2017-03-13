package de.bieniekconsulting.springcdi.bridge.support;

import org.springframework.context.ConfigurableApplicationContext;

import de.bieniekconsulting.springcdi.bridge.api.ApplicationContextProvider;

public class SharedStaticApplicationContextProvider implements ApplicationContextProvider {

	private static ConfigurableApplicationContext applicationContext;

	public static void setApplicationContext(final ConfigurableApplicationContext ctx) {
		applicationContext = ctx;
	}

	@Override
	public ConfigurableApplicationContext provideContext() {
		return applicationContext;
	}

}
