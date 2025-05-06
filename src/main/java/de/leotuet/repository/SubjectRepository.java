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

	public static void createTable(Connection databaseConnection) throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS subjects (" +
				"id INT AUTO_INCREMENT PRIMARY KEY NOT NULL, " +
				"name VARCHAR(255) NOT NULL)";
		Statement statement = databaseConnection.createStatement();
		statement.executeUpdate(query);
	}

	public Subject upsertAndGet(String name) {
		Subject existingSubject = getByAttributes(name);
		if (existingSubject != null) {
			return existingSubject;
		}

		String query = "INSERT INTO subjects (id, name) VALUES (default, ?)";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setString(1, name);
			statement.executeUpdate();
		} catch (SQLException e) {
			return null;
		}

		return getByAttributes(name);
	}

	public Subject getByAttributes(String name) {
		String query = "SELECT * FROM subjects WHERE name = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setString(1, name);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSetToSubject(resultSet);
			}
		} catch (SQLException e) {
			return null;
		}

		return null;
	}

	public Subject getById(int id) {
		String query = "SELECT * FROM subjects WHERE id = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSetToSubject(resultSet);
			}
		} catch (SQLException e) {
			return null;
		}

		return null;
	}

	public List<Subject> getAll() {
		List<Subject> subjects = new ArrayList<>();
		String query = "SELECT * FROM subjects";
		try {
			Statement statement = databaseConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				subjects.add(resultSetToSubject(resultSet));
			}
		} catch (SQLException e) {
			return subjects;
		}

		return subjects;
	}

	private Subject resultSetToSubject(ResultSet resultSet) throws SQLException {
		return new Subject(
				resultSet.getInt("id"),
				resultSet.getString("name"));
	}
}
