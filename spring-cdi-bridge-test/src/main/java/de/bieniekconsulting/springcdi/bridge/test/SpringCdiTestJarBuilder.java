package de.bieniekconsulting.springcdi.bridge.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import de.bieniekconsulting.springcdi.bridge.CdiScope;
import de.bieniekconsulting.springcdi.bridge.DependencyRegisteringBeanFactoryPostProcessor;
import de.bieniekconsulting.springcdi.bridge.SpringBean;
import de.bieniekconsulting.springcdi.bridge.SpringCdiExtension;

public class SpringCdiTestJarBuilder {
	public static JavaArchive extensionJar() {
		return ShrinkWrap.create(JavaArchive.class).addClass(CdiScope.class)
				.addClass(DependencyRegisteringBeanFactoryPostProcessor.class).addClass(SpringBean.class)
				.addClass(SpringCdiExtension.class)
				.addAsManifestResource(new StringAsset(SpringCdiExtension.class.getName()),
						"services/javax.enterprise.inject.spi.Extension");
	}
}
