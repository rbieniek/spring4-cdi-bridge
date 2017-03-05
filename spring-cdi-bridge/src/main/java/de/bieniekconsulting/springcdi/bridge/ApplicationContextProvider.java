package de.bieniekconsulting.springcdi.bridge;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public interface ApplicationContextProvider {
	ConfigurableApplicationContext provideContext();

	void cdiEnhancedContext(ApplicationContext context);
}
