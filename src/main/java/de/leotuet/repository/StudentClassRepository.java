package de.leotuet.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.leotuet.models.StudentClass;

public class StudentClassRepository {
	private final Connection databaseConnection;

	public StudentClassRepository(Connection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	public void createTable() throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS student_classes (" +
				"id SERIAL PRIMARY KEY, " +
				"year INT NOT NULL, " +
				"specialization VARCHAR(255) NOT NULL)";
		Statement statement = databaseConnection.createStatement();
		statement.executeUpdate(query);
	}

	public StudentClass upsertAndGet(int year, String specialization) {
		StudentClass existingClass = getByAttributes(year, specialization);
		if (existingClass != null) {
			return existingClass;
		}

		String query = "INSERT INTO student_classes (id, year, specialization) VALUES (default, ?, ?)";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, year);
			statement.setString(2, specialization);
			statement.executeUpdate();
		} catch (SQLException e) {
			return null;
		}

		return getByAttributes(year, specialization);
	}

	public StudentClass getByAttributes(int year, String specialization) {
		String query = "SELECT * FROM student_classes WHERE year = ? AND specialization = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, year);
			statement.setString(2, specialization);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSetToStudentClass(resultSet);
			}
		} catch (SQLException e) {
			return null;
		}

		return null;
	}

	public StudentClass getById(int id) {
		String query = "SELECT * FROM student_classes WHERE id = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSetToStudentClass(resultSet);
			}
		} catch (SQLException e) {
			return null;
		}

		return null;
	}

	public List<StudentClass> getAll() {
		List<StudentClass> studentClasses = new ArrayList<>();
		String query = "SELECT * FROM student_classes";
		try {
			Statement statement = databaseConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				studentClasses.add(resultSetToStudentClass(resultSet));
			}
		} catch (SQLException e) {
			return studentClasses;
		}

		return studentClasses;
	}

	public void deleteById(int id) throws SQLException {
		String query = "DELETE FROM student_classes WHERE id = ?";
		PreparedStatement statement = databaseConnection.prepareStatement(query);
		statement.setInt(1, id);
		statement.executeUpdate();
	}

	private StudentClass resultSetToStudentClass(ResultSet resultSet) throws SQLException {
		return new StudentClass(
				resultSet.getInt("id"),
				resultSet.getInt("year"),
				resultSet.getString("specialization").charAt(0));
	}

}
