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

	public static void createTable(Connection databaseConnection) throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS time_slots (" +
				"id INT AUTO_INCREMENT PRIMARY KEY NOT NULL, " +
				"week_day VARCHAR(255) NOT NULL, " +
				"start INT NOT NULL, " +
				"end INT NOT NULL)";
		Statement statement = databaseConnection.createStatement();
		statement.executeUpdate(query);
	}

	public TimeSlot upsertAndGet(String weekDay, int start, int end) {
		TimeSlot existingTimeSlot = getByAttributes(weekDay, start, end);
		if (existingTimeSlot != null) {
			return existingTimeSlot;
		}

		String query = "INSERT INTO time_slots (id, week_day, start, end) VALUES (default, ?, ?, ?)";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setString(1, weekDay);
			statement.setInt(2, start);
			statement.setInt(3, end);
			statement.executeUpdate();
		} catch (SQLException e) {
			return null;
		}

		return getByAttributes(weekDay, start, end);
	}

	public TimeSlot getByAttributes(String weekDay, int start, int end) {
		String query = "SELECT * FROM time_slots WHERE week_day = ? AND start = ? AND end = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setString(1, weekDay);
			statement.setInt(2, start);
			statement.setInt(3, end);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSetToTimeSlot(resultSet);
			}
		} catch (SQLException e) {
			return null;
		}

		return null;
	}

	public TimeSlot getById(int id) {
		String query = "SELECT * FROM time_slots WHERE id = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, id);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return resultSetToTimeSlot(resultSet);
				}
			}
		} catch (SQLException e) {
			return null;
		}

		return null;
	}

	public List<TimeSlot> getAll() {
		List<TimeSlot> timeSlots = new ArrayList<>();
		String query = "SELECT * FROM time_slots";
		try {
			Statement statement = databaseConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				timeSlots.add(resultSetToTimeSlot(resultSet));
			}
		} catch (SQLException e) {
			return timeSlots;
		}

		return timeSlots;
	}

	public void deleteById(int id) throws SQLException {
		String query = "DELETE FROM time_slots WHERE id = ?";
		PreparedStatement statement = databaseConnection.prepareStatement(query);
		statement.setInt(1, id);
		statement.executeUpdate();
	}

	private TimeSlot resultSetToTimeSlot(ResultSet resultSet) throws SQLException {
		return new TimeSlot(
				resultSet.getInt("id"),
				resultSet.getString("week_day"),
				resultSet.getInt("start"),
				resultSet.getInt("end"));
	}

}
