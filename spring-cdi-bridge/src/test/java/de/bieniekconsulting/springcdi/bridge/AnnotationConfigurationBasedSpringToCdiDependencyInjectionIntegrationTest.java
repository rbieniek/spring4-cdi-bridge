package de.bieniekconsulting.springcdi.bridge;

import static org.assertj.core.api.Assertions.assertThat;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bieniekconsulting.springcdi.bridge.spring.SpringScoped;
import de.bieniekconsulting.springcdi.bridge.support.SharedStaticApplicationContextProvider;
import de.bieniekconsulting.springcdi.bridge.support.WeldBootstrapRule;

public class AnnotationConfigurationBasedSpringToCdiDependencyInjectionIntegrationTest {

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

	@Test
	public void shouldFindExposedSpringBean() {
		assertThat(bootstrapRule.getWeldContainer().select(SpringExposedBean.class).get()).isNotNull();
	}

	@Test
	public void shouldNotFindHiddenSpringBean() {
		assertThat(bootstrapRule.getWeldContainer().select(SpringHiddenBean.class).isUnsatisfied()).isTrue();

	}

	public static class CdiBean {
		@Inject
		@ApplicationScoped
		private SpringExposedBean springBean;

		public boolean hasSpringBean() {
			return springBean != null;
		}
	}

	public static class SpringExposedBean {
	}

	public static class SpringHiddenBean {

	}

	@Configuration
	public static class TestConfig {

		@Bean
		@SpringScoped
		public SpringExposedBean springExposedBean() {
			return new SpringExposedBean();
		}

		@Bean
		public SpringHiddenBean springHiddedBean() {
			return new SpringHiddenBean();
		}
	}
}
