package de.bieniekconsulting.springcdi.wildfly.spring;

import javax.inject.Inject;

public class SpringGreeter {

	@Inject
	private NameBean nameBean;

	public String createGreeting(final String name) {
		return "Hello, " + nameBean.name() + "!";
	}

}
