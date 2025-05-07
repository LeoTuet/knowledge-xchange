package de.leotuet.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.leotuet.models.Group;

public class GroupRepository {
	private final Connection databaseConnection;

	public GroupRepository(Connection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	public static void createTable(Connection databaseConnection) throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS tutoring_groups (" +
				"id INT AUTO_INCREMENT PRIMARY KEY NOT NULL," +
				"offer_id INT NOT NULL," +
				"time_slot_id INT NOT NULL," +
				"FOREIGN KEY (offer_id) REFERENCES tutoring_offers(id)," +
				"FOREIGN KEY (time_slot_id) REFERENCES time_slots(id))";
		try (Statement statement = databaseConnection.createStatement()) {
			statement.executeUpdate(query);
		}
	}

	public Group create(int offerId, int timeSlotId) {
		String query = "INSERT INTO tutoring_groups (id, offer_id, time_slot_id) VALUES (default, ?, ?)";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, offerId);
			statement.setInt(2, timeSlotId);
			statement.executeUpdate();
		} catch (SQLException e) {
			return null;
		}

		return getByAttributes(offerId, timeSlotId);
	}

	public Group getByAttributes(int offerId, int timeSlotId) {
		String query = "SELECT * FROM tutoring_groups WHERE offer_id = ? AND time_slot_id = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, offerId);
			statement.setInt(2, timeSlotId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSetTooGroup(resultSet);
			}
		} catch (SQLException e) {
			return null;
		}

		return null;
	}

	public Group getById(int id) {
		String query = "SELECT * FROM tutoring_groups WHERE id = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSetTooGroup(resultSet);
			}
		} catch (SQLException e) {
			return null;
		}
		return null;
	}

	public List<Group> getAll() {
		List<Group> groups = new ArrayList<>();
		String query = "SELECT * FROM tutoring_groups";
		try {
			Statement statement = databaseConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				groups.add(resultSetTooGroup(resultSet));
			}
		} catch (SQLException e) {
			return groups;
		}

		return groups;
	}

	public void deleteById(int id) throws SQLException {
		String query = "DELETE FROM tutoring_groups WHERE id = ?";
		PreparedStatement statement = databaseConnection.prepareStatement(query);
		statement.setInt(1, id);
		statement.executeUpdate();
	}

	public void deleteAll() {
		String query = "DELETE FROM tutoring_groups";
		try {
			Statement statement = databaseConnection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Group resultSetTooGroup(ResultSet resultSet) throws SQLException {
		return new Group(
				resultSet.getInt("id"),
				resultSet.getInt("offer_id"),
				resultSet.getInt("time_slot_id"));
	}
}
