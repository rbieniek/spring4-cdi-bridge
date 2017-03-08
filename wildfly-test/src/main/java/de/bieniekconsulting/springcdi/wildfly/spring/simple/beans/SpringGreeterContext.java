package de.bieniekconsulting.springcdi.wildfly.spring.simple.beans;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = SpringGreeterContext.class)
public class SpringGreeterContext {

}
