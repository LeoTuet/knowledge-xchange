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

	public static void createTable(Connection databaseConnection) throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS tutoring_offers (" +
				"id INT AUTO_INCREMENT PRIMARY KEY NOT NULL, " +
				"tutor_id INT NOT NULL, " +
				"subject_id INT NOT NULL, " +
				"FOREIGN KEY (subject_id) REFERENCES subjects(id), " +
				"FOREIGN KEY (tutor_id) REFERENCES students(id))";
		Statement statement = databaseConnection.createStatement();
		statement.executeUpdate(query);
	}

	public TutoringOffer createAndGet(int tutorId, int subjectId) {
		String query = "INSERT INTO tutoring_offers (id, tutor_id, subject_id) VALUES (default, ?, ?)";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, tutorId);
			statement.setInt(2, subjectId);
			statement.executeUpdate();
		} catch (SQLException e) {
			return null;
		}

		return getByAttributes(tutorId, subjectId);
	}

	public TutoringOffer getByAttributes(int tutorId, int subjectId) {
		String query = "SELECT * FROM tutoring_offers WHERE tutor_id = ? AND subject_id = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, tutorId);
			statement.setInt(2, subjectId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSetToTutoringOffer(resultSet);
			}
		} catch (SQLException e) {
			return null;
		}

		return null;
	}

	public TutoringOffer getById(int id) {
		String query = "SELECT * FROM tutoring_offers WHERE id = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSetToTutoringOffer(resultSet);
			}
		} catch (SQLException e) {
			return null;
		}

		return null;

	}

	public List<TutoringOffer> getAll() {
		List<TutoringOffer> tutoringOffers = new ArrayList<>();
		String query = "SELECT * FROM tutoring_offers";
		try {
			Statement statement = databaseConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				tutoringOffers.add(resultSetToTutoringOffer(resultSet));
			}
		} catch (SQLException e) {
			return tutoringOffers;
		}

		return tutoringOffers;
	}

	public void deleteById(int id) throws SQLException {
		String query = "DELETE FROM tutoring_offers WHERE id = ?";
		PreparedStatement statement = databaseConnection.prepareStatement(query);
		statement.setInt(1, id);
		statement.executeUpdate();
	}

	private TutoringOffer resultSetToTutoringOffer(ResultSet resultSet) throws SQLException {
		return new TutoringOffer(
				resultSet.getInt("id"),
				resultSet.getInt("tutor_id"),
				resultSet.getInt("subject_id"));
	}
}
