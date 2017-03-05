package de.bieniekconsulting.springcdi.bridge;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

public class EmptyApplicationContextIntegrationTest {

	@BeforeClass
	public static void beforeClass() {
		SharedStaticApplicationContextProvider
				.setApplicationContext(new AnnotationConfigApplicationContext(TestConfig.class));
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
	}

	public static class Single {

	}

	@Configuration
	public static class TestConfig {

	}
}
