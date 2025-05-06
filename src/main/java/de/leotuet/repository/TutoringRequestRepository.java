package de.leotuet.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import de.leotuet.models.TutoringRequest;

public class TutoringRequestRepository {
	private final Connection databaseConnection;

	public TutoringRequestRepository(Connection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	public static void createTable(Connection databaseConnection) throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS tutoring_requests (" +
				"id INT AUTO_INCREMENT PRIMARY KEY NOT NULL, " +
				"student_id INT NOT NULL, " +
				"subject_id INT NOT NULL, " +
				"preferred_tutor_id INT NULL, " +
				"FOREIGN KEY (student_id) REFERENCES students(id), " +
				"FOREIGN KEY (subject_id) REFERENCES subjects(id), " +
				"FOREIGN KEY (preferred_tutor_id) REFERENCES students(id))";
		Statement statement = databaseConnection.createStatement();
		statement.executeUpdate(query);
	}

	public TutoringRequest createAndGet(int studentId, int subjectId, Integer preferredTutorId) {
		String query = "INSERT INTO tutoring_requests (id, student_id, subject_id, preferred_tutor_id) VALUES (default, ?, ?, ?)";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, studentId);
			statement.setInt(2, subjectId);
			if (preferredTutorId != null) {
				statement.setInt(3, preferredTutorId);
			} else {
				statement.setNull(3, Types.INTEGER);
			}
			statement.executeUpdate();
		} catch (SQLException e) {
			return null;
		}

		return getByAttributes(studentId, subjectId, preferredTutorId);
	}

	public TutoringRequest getByAttributes(int studentId, int subjectId, Integer preferredTutorId) {
		String query = "SELECT * FROM tutoring_requests WHERE student_id = ? AND subject_id = ?";

		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, studentId);
			statement.setInt(2, subjectId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSetToTutoringRequest(resultSet);
			}
		} catch (SQLException e) {
			return null;
		}

		return null;
	}

	public TutoringRequest getById(int id) {
		String query = "SELECT * FROM tutoring_requests WHERE id = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSetToTutoringRequest(resultSet);
			}
		} catch (SQLException e) {
			return null;
		}

		return null;
	}

	public List<TutoringRequest> getAll() {
		List<TutoringRequest> requests = new ArrayList<>();
		String query = "SELECT * FROM tutoring_requests";
		try {
			Statement statement = databaseConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				requests.add(resultSetToTutoringRequest(resultSet));
			}
		} catch (SQLException e) {
			return requests;
		}

		return requests;
	}

	public void deleteById(int id) throws SQLException {
		String query = "DELETE FROM tutoring_requests WHERE id = ?";
		PreparedStatement statement = databaseConnection.prepareStatement(query);
		statement.setInt(1, id);
		statement.executeUpdate();
	}

	private TutoringRequest resultSetToTutoringRequest(ResultSet resultSet) throws SQLException {
		return new TutoringRequest(
				resultSet.getInt("id"),
				resultSet.getInt("student_id"),
				resultSet.getInt("subject_id"),
				resultSet.getObject("preferred_tutor_id", Integer.class));
	}
}
