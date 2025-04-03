package de.leotuet.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.leotuet.models.Subject;

public class SubjectRepository {
	private final Connection databaseConnection;

	public SubjectRepository(Connection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	public void createTable() throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS subjects (" +
				"id SERIAL PRIMARY KEY, " +
				"name VARCHAR(255) NOT NULL)";
		try (Statement statement = databaseConnection.createStatement()) {
			statement.executeUpdate(query);
		}
	}

	public Subject create(String name) throws SQLException {
		String query = "INSERT INTO subjects (id, name) VALUES (default, ?)";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setString(1, name);
			statement.executeUpdate();
		}
		return getByName(name);
	}

	public Subject getByName(String name) throws SQLException {
		String query = "SELECT * FROM subjects WHERE name = ?";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setString(1, name);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Subject(
							resultSet.getInt("id"),
							resultSet.getString("name"));
				}
			}
		}
		return null;
	}

	public Subject getById(int id) throws SQLException {
		String query = "SELECT * FROM subjects WHERE id = ?";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setInt(1, id);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Subject(
							resultSet.getInt("id"),
							resultSet.getString("name"));
				}
			}
		}
		return null;
	}

	public List<Subject> getAll() throws SQLException {
		List<Subject> subjects = new ArrayList<>();
		String query = "SELECT * FROM subjects";
		try (Statement statement = databaseConnection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {
			while (resultSet.next()) {
				subjects.add(new Subject(
						resultSet.getInt("id"),
						resultSet.getString("name")));
			}
		}
		return subjects;
	}
}
