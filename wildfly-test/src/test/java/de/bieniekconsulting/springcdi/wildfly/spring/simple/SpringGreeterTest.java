package de.bieniekconsulting.springcdi.wildfly.spring.simple;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bieniekconsulting.springcdi.bridge.test.TestJarBuilder;
import de.bieniekconsulting.springcdi.wildfly.spring.simple.beans.NameBean;
import de.bieniekconsulting.springcdi.wildfly.spring.simple.beans.SimpleSpringApplicationContextProvider;
import de.bieniekconsulting.springcdi.wildfly.spring.simple.beans.SpringGreeterContext;

@RunWith(Arquillian.class)
public class SpringGreeterTest {

	@Deployment
	public static WebArchive createDeployment() {
		final MavenResolverSystem resolver = Maven.resolver();

		return ShrinkWrap.create(WebArchive.class).addClass(SpringGreeter.class)
				.addAsLibraries(
						ShrinkWrap.create(JavaArchive.class).addClass(SimpleSpringApplicationContextProvider.class)
								.addClass(SpringGreeterContext.class).addClass(NameBean.class).addAsManifestResource(
										new StringAsset(SimpleSpringApplicationContextProvider.class.getName()),
										"services/de.bieniekconsulting.springcdi.bridge.support.ApplicationContextProvider"))
				.addAsLibraries(TestJarBuilder.extensionJar()).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.addAsLibraries(resolver
						.resolve("org.apache.commons:commons-lang3:3.5",
								"org.springframework:spring-beans:4.3.7.RELEASE",
								"org.springframework:spring-core:4.3.7.RELEASE",
								"org.springframework:spring-context:4.3.7.RELEASE",
								"org.springframework:spring-context-support:4.3.7.RELEASE")
						.withTransitivity().as(JavaArchive.class));
	}

	@Inject
	private SpringGreeter greeter;

	@Test
	public void shouldCreateGreeting() {
		// setup
		final String name = "spring";

		// when
		final String result = greeter.createGreeting(name);

		// then
		assertThat(result, equalTo("Hello, " + name + "!"));
	}
}
