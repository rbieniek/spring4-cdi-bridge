package de.bieniekconsulting.springcdi.bridge.api.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import de.bieniekconsulting.springcdi.bridge.api.ApplicationContextProvider;
import de.bieniekconsulting.springcdi.bridge.api.Cdi;
import de.bieniekconsulting.springcdi.bridge.api.SpringScoped;


public class SpringCdiApiTestJarBuilder {
	public static JavaArchive jar() {
		return ShrinkWrap.create(JavaArchive.class).addClass(Cdi.class).addClass(SpringScoped.class)
				.addClass(ApplicationContextProvider.class);
	}
}
