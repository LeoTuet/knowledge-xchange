package de.leotuet.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.leotuet.models.GroupStudent;

public class GroupStudentRepository {
	private final Connection databaseConnection;

	public GroupStudentRepository(Connection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	public static void createTable(Connection databaseConnection) throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS tutoring_group_students (" +
				"group_id INT NOT NULL, " +
				"request_id INT NOT NULL, " +
				"PRIMARY KEY (group_id, request_id), " +
				"FOREIGN KEY (group_id) REFERENCES tutoring_groups(id), " +
				"FOREIGN KEY (request_id) REFERENCES tutoring_requests(id))";
		try (Statement statement = databaseConnection.createStatement()) {
			statement.executeUpdate(query);
		}
	}

	public GroupStudent create(int groupId, int requestId) {
		String query = "INSERT INTO tutoring_group_students (group_id, request_id) VALUES (?, ?)";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, groupId);
			statement.setInt(2, requestId);
			statement.executeUpdate();

		} catch (SQLException e) {
			return null;
		}

		return getByAttributes(groupId, requestId);
	}

	public GroupStudent getByAttributes(int groupId, int requestId) {
		String query = "SELECT * FROM tutoring_group_students WHERE group_id = ? AND request_id = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, groupId);
			statement.setInt(2, requestId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSetToGroupStudent(resultSet);
			}
		} catch (SQLException e) {
			return null;
		}

		return null;
	}

	public List<GroupStudent> getAllByGroupId(int groupId) {
		List<GroupStudent> groupStudents = new ArrayList<>();
		String query = "SELECT * FROM tutoring_group_students WHERE group_id = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, groupId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				groupStudents.add(resultSetToGroupStudent(resultSet));
			}
		} catch (SQLException e) {
			return null;
		}

		return groupStudents;
	}

	public void deleteByGroupId(int groupId) throws SQLException {
		String query = "DELETE FROM tutoring_group_students WHERE group_id = ?";
		PreparedStatement statement = databaseConnection.prepareStatement(query);
		statement.setInt(1, groupId);
		statement.executeUpdate();
	}

	public void deleteAll() {
		String query = "DELETE FROM tutoring_group_students";
		try {
			Statement statement = databaseConnection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public GroupStudent resultSetToGroupStudent(ResultSet resultSet) throws SQLException {
		return new GroupStudent(
				resultSet.getInt("group_id"),
				resultSet.getInt("request_id"));
	}
}
