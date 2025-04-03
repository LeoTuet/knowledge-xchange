package de.leotuet.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.leotuet.models.TutoringOffer;

public class TutoringOfferRepository {
	private final Connection databaseConnection;

	public TutoringOfferRepository(Connection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	public void createTable() throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS tutoring_offers (" +
				"id SERIAL PRIMARY KEY, " +
				"tutor_id INT NOT NULL, " +
				"subject_id INT NOT NULL, " +
				"FOREIGN KEY (subject_id) REFERENCES subjects(id))";
		try (Statement statement = databaseConnection.createStatement()) {
			statement.executeUpdate(query);
		}
	}

	public void create(int tutorId, int subjectId) throws SQLException {
		String query = "INSERT INTO tutoring_offers (id, tutor_id, subject_id) VALUES (default, ?, ?)";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setInt(1, tutorId);
			statement.setInt(2, subjectId);
			statement.executeUpdate();
		}
	}

	public TutoringOffer getById(int id) throws SQLException {
		String query = "SELECT * FROM tutoring_offers WHERE id = ?";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setInt(1, id);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new TutoringOffer(
							resultSet.getInt("id"),
							resultSet.getInt("tutor_id"),
							resultSet.getInt("subject_id"));
				}
			}
		}
		return null;
	}

	public List<TutoringOffer> getAll() throws SQLException {
		List<TutoringOffer> tutoringOffers = new ArrayList<>();
		String query = "SELECT * FROM tutoring_offers";
		try (Statement statement = databaseConnection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {
			while (resultSet.next()) {
				tutoringOffers.add(new TutoringOffer(
						resultSet.getInt("id"),
						resultSet.getInt("tutor_id"),
						resultSet.getInt("subject_id")));
			}
		}
		return tutoringOffers;
	}

	public void deleteById(int id) throws SQLException {
		String query = "DELETE FROM tutoring_offers WHERE id = ?";
		try (PreparedStatement statement = databaseConnection.prepareStatement(query)) {
			statement.setInt(1, id);
			statement.executeUpdate();
		}
	}
}
