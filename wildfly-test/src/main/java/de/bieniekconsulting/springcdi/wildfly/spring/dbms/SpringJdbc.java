package de.bieniekconsulting.springcdi.wildfly.spring.dbms;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.bieniekconsulting.springcdi.wildfly.spring.dbms.beans.DatabaseService;

public class SpringJdbc {
	@Inject
	@ApplicationScoped
	private DatabaseService databaseService;

	public UUID putString(final String value) {
		return databaseService.storeString(value);
	}

	public String getString(final UUID key) {
		return databaseService.retrieveString(key).orElse(null);
	}

}
