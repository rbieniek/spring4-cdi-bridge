package de.bieniekconsulting.springcdi.bridge;

import static org.assertj.core.api.Assertions.assertThat;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import de.bieniekconsulting.springcdi.bridge.support.SharedStaticApplicationContextProvider;
import de.bieniekconsulting.springcdi.bridge.support.WeldBootstrapRule;

public class CdiToSpringInjectionIntegrationTest {

	private static AnnotationConfigApplicationContext applicationContext;

	@BeforeClass
	public static void beforeClass() {
		applicationContext = new AnnotationConfigApplicationContext();

		applicationContext.register(TestConfig.class);

		assertThat(applicationContext.isActive()).isFalse();

		SharedStaticApplicationContextProvider.setApplicationContext(applicationContext);
	}

	@Rule
	public WeldBootstrapRule bootstrapRule = new WeldBootstrapRule(CdiBeanProducer.class, CdiConsumerBean.class);

	// @Test
	public void shouldHaveAutowiredCdiBean() {
		assertThat(applicationContext.isActive()).isTrue();

		final CdiBean cdiBean = applicationContext.getBean(CdiBean.class);

		assertThat(cdiBean).isNotNull();
	}

	@Test
	public void shouldHaveInjectedCdiBean() {
		assertThat(bootstrapRule.getWeldContainer().select(CdiConsumerBean.class).isUnsatisfied()).isFalse();
	}

	@Dependent
	public static class CdiBeanProducer {
		@Produces
		@Dependent
		public CdiBean cdiBean() {
			return new CdiBean();
		}
	}

	public static class CdiBean {
	}

	public static class CdiConsumerBean {
		@Inject
		private CdiBean cdiBean;

		public boolean haveCdiBean() {
			return cdiBean != null;
		}
	}

	@Configuration
	public static class TestConfig {

	}
}
