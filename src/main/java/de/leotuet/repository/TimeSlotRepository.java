package de.leotuet.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.leotuet.models.TimeSlot;

public class TimeSlotRepository {
	private final Connection databaseConnection;

	public TimeSlotRepository(Connection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	public void createTable() throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS time_slots (" +
				"id SERIAL PRIMARY KEY, " +
				"week_day VARCHAR(255) NOT NULL, " +
				"start INT NOT NULL, " +
				"end INT NOT NULL)";
		try (Statement statement = databaseConnection.createStatement()) {
			statement.executeUpdate(query);
		}
	}

	public void create(String weekDay, int start, int end) throws SQLException {
		String query = "INSERT INTO time_slots (id, week_day, start, end) VALUES (default, ?, ?, ?)";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setString(1, weekDay);
			statement.setInt(2, start);
			statement.setInt(3, end);
			statement.executeUpdate();
		}
	}

	public TimeSlot getById(int id) throws SQLException {
		String query = "SELECT * FROM time_slots WHERE id = ?";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setInt(1, id);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new TimeSlot(
							resultSet.getInt("id"),
							resultSet.getString("week_day"),
							resultSet.getInt("start"),
							resultSet.getInt("end"));
				}
			}
		}
		return null;
	}

	public List<TimeSlot> getAll() throws SQLException {
		List<TimeSlot> timeSlots = new ArrayList<>();
		String query = "SELECT * FROM time_slots";
		try (Statement statement = databaseConnection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {
			while (resultSet.next()) {
				timeSlots.add(new TimeSlot(
						resultSet.getInt("id"),
						resultSet.getString("week_day"),
						resultSet.getInt("start"),
						resultSet.getInt("end")));
			}
		}
		return timeSlots;
	}

	public void deleteById(int id) throws SQLException {
		String query = "DELETE FROM time_slots WHERE id = ?";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setInt(1, id);
			statement.executeUpdate();
		}
	}
}
