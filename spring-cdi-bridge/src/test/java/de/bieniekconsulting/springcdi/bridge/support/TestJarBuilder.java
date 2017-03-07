package de.bieniekconsulting.springcdi.bridge.support;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import de.bieniekconsulting.springcdi.bridge.CdiScope;
import de.bieniekconsulting.springcdi.bridge.DependencyRegisteringBeanFactoryPostProcessor;
import de.bieniekconsulting.springcdi.bridge.SpringBean;
import de.bieniekconsulting.springcdi.bridge.SpringCdiExtension;
import de.bieniekconsulting.springcdi.bridge.spring.Cdi;
import de.bieniekconsulting.springcdi.bridge.spring.SpringScoped;

public class TestJarBuilder {
	public static JavaArchive extensionJar() {
		return ShrinkWrap.create(JavaArchive.class).addClass(CdiScope.class)
				.addClass(DependencyRegisteringBeanFactoryPostProcessor.class).addClass(SpringBean.class)
				.addClass(SpringCdiExtension.class).addClass(Cdi.class).addClass(SpringScoped.class)
				.addClass(ApplicationContextProvider.class)
				.addAsManifestResource("META-INF/services/javax.enterprise.inject.spi.Extension");
	}
}
