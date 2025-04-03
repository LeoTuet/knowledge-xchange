package de.leotuet.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.leotuet.models.TutoringRequestTimeSlot;

public class TutoringRequestTimeSlotRepository {
	private final Connection databaseConnection;

	public TutoringRequestTimeSlotRepository(Connection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	public void createTable() throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS tutoring_request_time_slots (" +
				"request_id INT NOT NULL, " +
				"time_slot_id INT NOT NULL, " +
				"PRIMARY KEY (request_id, time_slot_id), " +
				"FOREIGN KEY (request_id) REFERENCES tutoring_requests(id), " +
				"FOREIGN KEY (time_slot_id) REFERENCES time_slots(id))";
		try (Statement statement = databaseConnection.createStatement()) {
			statement.executeUpdate(query);
		}
	}

	public void create(int requestId, int timeSlotId) throws SQLException {
		String query = "INSERT INTO tutoring_request_time_slots (request_id, time_slot_id) VALUES (?, ?)";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setInt(1, requestId);
			statement.setInt(2, timeSlotId);
			statement.executeUpdate();
		}
	}

	public List<TutoringRequestTimeSlot> getByRequestId(int requestId) throws SQLException {
		List<TutoringRequestTimeSlot> timeSlots = new ArrayList<>();
		String query = "SELECT * FROM tutoring_request_time_slots WHERE request_id = ?";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setInt(1, requestId);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					timeSlots.add(new TutoringRequestTimeSlot(
							resultSet.getInt("request_id"),
							resultSet.getInt("time_slot_id")));
				}
			}
		}
		return timeSlots;
	}

	public void deleteByRequestId(int requestId) throws SQLException {
		String query = "DELETE FROM tutoring_request_time_slots WHERE request_id = ?";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setInt(1, requestId);
			statement.executeUpdate();
		}
	}
}
