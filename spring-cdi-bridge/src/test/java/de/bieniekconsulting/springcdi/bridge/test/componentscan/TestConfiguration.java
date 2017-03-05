package de.bieniekconsulting.springcdi.bridge.test.componentscan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = TestConfiguration.class)
public class TestConfiguration {

}
