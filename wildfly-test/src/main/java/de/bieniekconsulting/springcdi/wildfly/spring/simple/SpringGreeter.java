package de.bieniekconsulting.springcdi.wildfly.spring.simple;

import javax.inject.Inject;

import de.bieniekconsulting.springcdi.wildfly.spring.simple.beans.NameBean;

public class SpringGreeter {

	@Inject
	private NameBean nameBean;

	public String createGreeting(final String name) {
		return "Hello, " + nameBean.name() + "!";
	}

}
