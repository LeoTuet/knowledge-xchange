package de.leotuet.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.leotuet.models.Student;

public class StudentRepository {
	private final Connection databaseConnection;

	public StudentRepository(Connection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	public void createTable() throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS students (" +
				"id SERIAL PRIMARY KEY, " +
				"first_name VARCHAR(255) NOT NULL, " +
				"last_name VARCHAR(255) NOT NULL, " +
				"student_class_id INT NOT NULL, " +
				"FOREIGN KEY (student_class_id) REFERENCES student_classes(id))";
		Statement statement = databaseConnection.createStatement();
		statement.executeUpdate(query);
	}

	public Student create(String firstName, String lastName, int studentClassId) {
		String query = "INSERT INTO students (id, first_name, last_name, student_class_id) VALUES (default, ?, ?, ?)";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setString(1, firstName);
			statement.setString(2, lastName);
			statement.setInt(3, studentClassId);
			statement.executeUpdate();
		} catch (SQLException e) {
			return null;
		}

		return getByAttributes(firstName, lastName, studentClassId);
	}

	public Student getByAttributes(String firstName, String lastName, int studentClassId) {
		String query = "SELECT * FROM students WHERE first_name = ? AND last_name = ? AND student_class_id = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setString(1, firstName);
			statement.setString(2, lastName);
			statement.setInt(3, studentClassId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSetToStudent(resultSet);
			}
		} catch (SQLException e) {
			return null;
		}

		return null;
	}

	public Student getById(int id) {
		String query = "SELECT * FROM students WHERE id = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSetToStudent(resultSet);
			}
		} catch (SQLException e) {
			return null;
		}

		return null;
	}

	public List<Student> getAll() {
		List<Student> students = new ArrayList<>();
		String query = "SELECT * FROM students";
		try {
			Statement statement = databaseConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				students.add(resultSetToStudent(resultSet));
			}
		} catch (SQLException e) {
			return students;
		}

		return students;
	}

	public void deleteById(int id) throws SQLException {
		String query = "DELETE FROM students WHERE id = ?";
		PreparedStatement statement = databaseConnection.prepareStatement(query);
		statement.setInt(1, id);
		statement.executeUpdate();
	}

	private Student resultSetToStudent(ResultSet resultSet) throws SQLException {
		return new Student(
				resultSet.getInt("id"),
				resultSet.getString("first_name"),
				resultSet.getString("last_name"),
				resultSet.getInt("student_class_id"));
	}
}
