package de.bieniekconsulting.springcdi.bridge;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public class SharedStaticApplicationContextProvider implements ApplicationContextProvider {

	private static ConfigurableApplicationContext applicationContext;

	public static void setApplicationContext(final ConfigurableApplicationContext ctx) {
		applicationContext = ctx;
	}

	@Override
	public ConfigurableApplicationContext provideContext() {
		return applicationContext;
	}

	@Override
	public void cdiEnhancedContext(final ApplicationContext context) {
		// TODO Auto-generated method stub

	}

}
