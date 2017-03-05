package de.bieniekconsulting.springcdi.bridge;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import de.bieniekconsulting.springcdi.bridge.spring.SpringScoped;
import de.bieniekconsulting.springcdi.bridge.support.SharedStaticApplicationContextProvider;
import de.bieniekconsulting.springcdi.bridge.support.WeldBootstrapRule;
import de.bieniekconsulting.springcdi.bridge.test.componentscan.SpringExposedBean;
import de.bieniekconsulting.springcdi.bridge.test.componentscan.SpringHiddenBean;
import de.bieniekconsulting.springcdi.bridge.test.componentscan.TestConfiguration;

public class MixedConfigurationBasedSpringToCdiDependencyInjectionIntegrationTest {

	private static AnnotationConfigApplicationContext applicationContext;

	@BeforeClass
	public static void beforeClass() {
		applicationContext = new AnnotationConfigApplicationContext();

		applicationContext.register(TestConfig.class);

		assertThat(applicationContext.isActive()).isFalse();

		SharedStaticApplicationContextProvider.setApplicationContext(applicationContext);
	}

	@Rule
	public WeldBootstrapRule bootstrapRule = new WeldBootstrapRule();

	@Test
	public void shouldFindExposedSpringBean() {
		assertThat(bootstrapRule.getWeldContainer().select(SpringExposedBean.class).isUnsatisfied()).isFalse();
		assertThat(bootstrapRule.getWeldContainer().select(FactorySpringExposedBean.class).isUnsatisfied()).isFalse();
	}

	@Test
	public void shouldNotFindHiddenSpringBean() {
		assertThat(bootstrapRule.getWeldContainer().select(SpringHiddenBean.class).isUnsatisfied()).isTrue();
		assertThat(bootstrapRule.getWeldContainer().select(FactorySpringHiddenBean.class).isUnsatisfied()).isTrue();

	}

	public static class FactorySpringExposedBean {
	}

	public static class FactorySpringHiddenBean {

	}

	@Configuration
	@Import(TestConfiguration.class)
	public static class TestConfig {

		@Bean
		@SpringScoped
		public FactorySpringExposedBean factorySpringExposedBean() {
			return new FactorySpringExposedBean();
		}

		@Bean
		public FactorySpringHiddenBean factorySpringHiddedBean() {
			return new FactorySpringHiddenBean();
		}
	}
}
