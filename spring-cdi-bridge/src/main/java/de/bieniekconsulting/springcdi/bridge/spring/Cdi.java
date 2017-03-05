package de.bieniekconsulting.springcdi.bridge.spring;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;

@Documented
@Retention(RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Scope(Cdi.SCOPE_CDI)
public @interface Cdi {
	public static final String SCOPE_CDI = "CDI";
}
