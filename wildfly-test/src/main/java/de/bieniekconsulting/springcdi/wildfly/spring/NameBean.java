package de.bieniekconsulting.springcdi.wildfly.spring;

import org.springframework.stereotype.Component;

import de.bieniekconsulting.springcdi.bridge.spring.SpringScoped;

@Component
@SpringScoped
public class NameBean {
	public String name() {
		return "spring";
	}
}
