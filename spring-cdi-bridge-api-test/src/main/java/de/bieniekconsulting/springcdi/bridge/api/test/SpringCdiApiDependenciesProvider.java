package de.bieniekconsulting.springcdi.bridge.api.test;

import java.util.HashSet;
import java.util.Set;

import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;

public class SpringCdiApiDependenciesProvider {

	public static Set<JavaArchive> dependencies() {
		final Set<JavaArchive> dependencies = new HashSet<>();
		final MavenResolverSystem resolver = Maven.resolver();
		
		for(JavaArchive archive: resolver.resolve("org.springframework:spring-context:4.3.7.RELEASE")
		.withTransitivity().as(JavaArchive.class)) {
			dependencies.add(archive);
		}

		
		return dependencies;
	}
}
