package de.bieniekconsulting.springcdi.wildfly.spring.dbms.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.bieniekconsulting.springcdi.bridge.api.SpringScoped;

@Component
@SpringScoped
public class DatabaseService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional
	public UUID storeString(final String value) {
		final UUID key = UUID.randomUUID();

		jdbcTemplate.update("INSERT INTO key_value_map (key, string_value) values (?,?)",
				(PreparedStatementSetter) ps -> {
					ps.setString(1, key.toString());
					ps.setString(2, value);
				});

		return key;
	}

	@Transactional
	public UUID storeNumber(final int value) {
		final UUID key = UUID.randomUUID();

		jdbcTemplate.update("INSERT INTO key_value_map (key, numeric_value) values (?,?)",
				(PreparedStatementSetter) ps -> {
					ps.setString(1, key.toString());
					ps.setInt(2, value);
				});

		return key;

	}

	@Transactional
	public Optional<String> retrieveString(final UUID key) {
		return jdbcTemplate.execute((PreparedStatementCreator) con -> {
			final PreparedStatement ps = con.prepareStatement("select string_value from key_value_map where key=?");

			ps.setString(1, key.toString());

			return ps;
		}, (PreparedStatementCallback<Optional<String>>) ps -> {
			ResultSet rs = null;

			try {
				rs = ps.executeQuery();

				if (rs.next()) {
					return Optional.of(rs.getString(1));
				} else {
					return Optional.empty();
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
		});
	}

	@Transactional
	public Optional<Integer> retrieveInteger(final UUID key) {
		return jdbcTemplate.execute((PreparedStatementCreator) con -> {
			final PreparedStatement ps = con.prepareStatement("select numeric_value from key_value_map where key=?");

			ps.setString(1, key.toString());

			return ps;
		}, (PreparedStatementCallback<Optional<Integer>>) ps -> {
			ResultSet rs = null;

			try {
				rs = ps.executeQuery();

				if (rs.next()) {
					return Optional.of(rs.getInt(1));
				} else {
					return Optional.empty();
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
		});

	}
}
