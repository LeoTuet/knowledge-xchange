package de.leotuet.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.leotuet.models.TutoringOfferTimeSlot;

public class TutoringOfferTimeSlotRepository {
	private final Connection databaseConnection;

	public TutoringOfferTimeSlotRepository(Connection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	public static void createTable(Connection databaseConnection) throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS tutoring_offer_time_slots (" +
				"offer_id INT NOT NULL, " +
				"time_slot_id INT NOT NULL, " +
				"PRIMARY KEY (offer_id, time_slot_id), " +
				"FOREIGN KEY (offer_id) REFERENCES tutoring_offers(id), " +
				"FOREIGN KEY (time_slot_id) REFERENCES time_slots(id))";
		Statement statement = databaseConnection.createStatement();
		statement.executeUpdate(query);
	}

	public void create(int offerId, int timeSlotId) {
		String query = "INSERT INTO tutoring_offer_time_slots (offer_id, time_slot_id) VALUES (?, ?)";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, offerId);
			statement.setInt(2, timeSlotId);
			statement.executeUpdate();
		} catch (SQLException e) {
		}

	}

	public List<TutoringOfferTimeSlot> getByOfferId(int offerId) {
		List<TutoringOfferTimeSlot> timeSlots = new ArrayList<>();
		String query = "SELECT * FROM tutoring_offer_time_slots WHERE offer_id = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, offerId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				timeSlots.add(resultSetToTutoringOfferTimeSlot(resultSet));
			}
		} catch (SQLException e) {
			return null;
		}

		return timeSlots;
	}

	public void deleteByOfferId(int offerId) throws SQLException {
		String query = "DELETE FROM tutoring_offer_time_slots WHERE offer_id = ?";
		PreparedStatement statement = databaseConnection.prepareStatement(query);
		statement.setInt(1, offerId);
		statement.executeUpdate();
	}

	private TutoringOfferTimeSlot resultSetToTutoringOfferTimeSlot(ResultSet resultSet) throws SQLException {
		return new TutoringOfferTimeSlot(
				resultSet.getInt("offer_id"),
				resultSet.getInt("time_slot_id"));
	}
}
