package de.bieniekconsulting.springcdi.wildfly.spring.simple.beans;

import org.springframework.stereotype.Component;

import de.bieniekconsulting.springcdi.bridge.api.SpringScoped;

@Component
@SpringScoped
public class NameBean {
	public String name() {
		return "spring";
	}
}
