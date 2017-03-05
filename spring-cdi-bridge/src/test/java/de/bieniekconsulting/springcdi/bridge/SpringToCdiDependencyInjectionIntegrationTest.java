package de.bieniekconsulting.springcdi.bridge;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bieniekconsulting.springcdi.bridge.support.SharedStaticApplicationContextProvider;
import de.bieniekconsulting.springcdi.bridge.support.WeldBootstrapRule;

public class SpringToCdiDependencyInjectionIntegrationTest {

	private static AnnotationConfigApplicationContext applicationContext;

	@BeforeClass
	public static void beforeClass() {
		applicationContext = new AnnotationConfigApplicationContext();

		applicationContext.register(TestConfig.class);

		assertThat(applicationContext.isActive()).isFalse();

		SharedStaticApplicationContextProvider.setApplicationContext(applicationContext);
	}

	@Rule
	public WeldBootstrapRule bootstrapRule = new WeldBootstrapRule(CdiBean.class);

	private CdiBean cdiBean;

	@Before
	public void before() {
		cdiBean = bootstrapRule.getWeldContainer().select(CdiBean.class).get();
	}

	@Test
	public void shouldHaveInjectedSpringBean() {
		assertThat(cdiBean).isNotNull();
		assertThat(cdiBean.hasSpringBean()).isTrue();
	}

	public static class CdiBean {
		@Inject
		private SpringBean springBean;

		public boolean hasSpringBean() {
			return springBean != null;
		}
	}

	public static class SpringBean {
	}

	@Configuration
	public static class TestConfig {

		@Bean
		public SpringBean springBean() {
			return new SpringBean();
		}
	}
}
