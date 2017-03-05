package de.bieniekconsulting.springcdi.bridge;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bieniekconsulting.springcdi.bridge.support.SharedStaticApplicationContextProvider;
import de.bieniekconsulting.springcdi.bridge.support.WeldBootstrapRule;
import de.bieniekconsulting.springcdi.bridge.test.componentscan.SpringExposedBean;
import de.bieniekconsulting.springcdi.bridge.test.componentscan.SpringHiddenBean;
import de.bieniekconsulting.springcdi.bridge.test.componentscan.TestConfiguration;

public class ComponentScanBasedSpringToCdiDependencyInjectionIntegrationTest {

	private static AnnotationConfigApplicationContext applicationContext;

	@BeforeClass
	public static void beforeClass() {
		applicationContext = new AnnotationConfigApplicationContext();

		applicationContext.register(TestConfiguration.class);

		assertThat(applicationContext.isActive()).isFalse();

		SharedStaticApplicationContextProvider.setApplicationContext(applicationContext);
	}

	@Rule
	public WeldBootstrapRule bootstrapRule = new WeldBootstrapRule();

	@Test
	public void shouldFindExposedSpringBean() {
		assertThat(bootstrapRule.getWeldContainer().select(SpringExposedBean.class).isUnsatisfied()).isFalse();
	}

	@Test
	public void shouldNotFindHiddenSpringBean() {
		assertThat(bootstrapRule.getWeldContainer().select(SpringHiddenBean.class).isUnsatisfied()).isTrue();

	}
}
