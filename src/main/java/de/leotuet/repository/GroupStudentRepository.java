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

	public void createTable() throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS group_students (" +
				"group_id INT NOT NULL, " +
				"request_id INT NOT NULL, " +
				"PRIMARY KEY (group_id, request_id), " +
				"FOREIGN KEY (group_id) REFERENCES groups(id), " +
				"FOREIGN KEY (request_id) REFERENCES tutoring_requests(id))";
		try (Statement statement = databaseConnection.createStatement()) {
			statement.executeUpdate(query);
		}
	}

	public void create(int groupId, int requestId) throws SQLException {
		String query = "INSERT INTO group_students (group_id, request_id) VALUES (?, ?)";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setInt(1, groupId);
			statement.setInt(2, requestId);
			statement.executeUpdate();
		}
	}

	public List<GroupStudent> getByGroupId(int groupId) throws SQLException {
		List<GroupStudent> groupStudents = new ArrayList<>();
		String query = "SELECT * FROM group_students WHERE group_id = ?";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setInt(1, groupId);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					groupStudents.add(new GroupStudent(
							resultSet.getInt("group_id"),
							resultSet.getInt("request_id")));
				}
			}
		}
		return groupStudents;
	}

	public void deleteByGroupId(int groupId) throws SQLException {
		String query = "DELETE FROM group_students WHERE group_id = ?";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setInt(1, groupId);
			statement.executeUpdate();
		}
	}
}
