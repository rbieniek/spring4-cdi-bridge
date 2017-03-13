package de.bieniekconsulting.springcdi.wildfly.spring.dbms;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.UUID;

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

import de.bieniekconsulting.springcdi.bridge.api.test.SpringCdiApiTestJarBuilder;
import de.bieniekconsulting.springcdi.bridge.test.SpringCdiDependenciesProvider;
import de.bieniekconsulting.springcdi.bridge.test.SpringCdiTestJarBuilder;
import de.bieniekconsulting.springcdi.wildfly.spring.dbms.beans.DatabaseService;
import de.bieniekconsulting.springcdi.wildfly.spring.dbms.beans.JdbcContext;
import de.bieniekconsulting.springcdi.wildfly.spring.dbms.beans.JdbcSpringApplicationContextProvider;

@RunWith(Arquillian.class)
public class SpringJdbcTest {

	@Deployment
	public static WebArchive createDeployment() {
		final MavenResolverSystem resolver = Maven.resolver();

		return ShrinkWrap.create(WebArchive.class).addClass(SpringJdbc.class)
				.addAsLibraries(ShrinkWrap.create(JavaArchive.class)
						.addClass(JdbcSpringApplicationContextProvider.class).addClass(JdbcContext.class)
						.addClass(DatabaseService.class)
						.addAsManifestResource(new StringAsset(JdbcSpringApplicationContextProvider.class.getName()),
								"services/de.bieniekconsulting.springcdi.bridge.api.ApplicationContextProvider")
						.addAsResource("db-changelog.xml"))
				.addAsLibraries(SpringCdiTestJarBuilder.extensionJar(), SpringCdiApiTestJarBuilder.jar()).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.addAsWebInfResource("jboss-ds.xml")
				.addAsLibraries(SpringCdiDependenciesProvider.dependencies())
				.addAsLibraries(resolver
						.resolve("org.springframework:spring-jdbc:4.3.7.RELEASE",
								"org.springframework:spring-tx:4.3.7.RELEASE", "org.liquibase:liquibase-core:3.5.3")
						.withTransitivity().as(JavaArchive.class));
	}

	@Inject
	private SpringJdbc springJdbc;

	@Test
	public void shouldPutAndGetString() {
		final String value = "foo";
		final UUID key = springJdbc.putString(value);

		assertThat(key, notNullValue());

		final String value2 = springJdbc.getString(key);

		assertThat(value2, is(value));
	}
}
