package de.bieniekconsulting.springcdi.bridge.support;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class WeldBootstrapRule implements TestRule {

	private WeldContainer weldContainer;

	private Class<?>[] beanClasses;

	public WeldBootstrapRule(final Class<?>... beanClasses) {
		this.beanClasses = beanClasses;
	}

	public WeldContainer getWeldContainer() {
		return weldContainer;
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				final Weld weld = new Weld();

				if (beanClasses != null) {
					weld.beanClasses(beanClasses);
				}

				weldContainer = weld.initialize();

				try {
					base.evaluate();
				} finally {
					weld.shutdown();
				}

			}
		};
	}

}
