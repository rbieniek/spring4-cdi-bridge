package de.bieniekconsulting.springcdi.wildfly.simple;

public class Greeter {
	public String createGreeting(final String name) {
		return "Hello, " + name + "!";
	}
}