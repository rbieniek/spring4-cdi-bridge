package de.bieniekconsulting.springcdi.bridge;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import de.bieniekconsulting.springcdi.bridge.support.SharedStaticApplicationContextProvider;
import de.bieniekconsulting.springcdi.bridge.support.WeldBootstrapRule;

public class NonActiveEmptyApplicationContextIntegrationTest {

	private static AnnotationConfigApplicationContext applicationContext;

	@BeforeClass
	public static void beforeClass() {
		applicationContext = new AnnotationConfigApplicationContext();

		applicationContext.register(TestConfig.class);

		assertThat(applicationContext.isActive()).isFalse();

		SharedStaticApplicationContextProvider.setApplicationContext(applicationContext);
	}

	@Rule
	public WeldBootstrapRule bootstrapRule = new WeldBootstrapRule(Single.class);

	private Single single;

	@Before
	public void before() {
		single = bootstrapRule.getWeldContainer().select(Single.class).get();
	}

	@Test
	public void shouldHaveSingleInstance() {
		assertThat(single).isNotNull();
		assertThat(applicationContext.isActive()).isTrue();
	}

	public static class Single {

	}

	@Configuration
	public static class TestConfig {

	}
}
