package de.leotuet.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
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

	public List<TutoringOffer> getAllBySpecializationAndSubject(String specialization, String subject) {
		List<TutoringOffer> tutoringOffers = new ArrayList<>();
		String query = "SELECT * FROM tutoring_offers " +
				"JOIN students ON tutoring_offers.tutor_id = students.id " +
				"JOIN student_classes ON students.student_class_id = student_classes.id " +
				"JOIN subjects ON tutoring_offers.subject_id = subjects.id " +
				"WHERE student_classes.specialization = ? AND subjects.name = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setString(1, specialization);
			statement.setString(2, subject);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				tutoringOffers.add(resultSetToTutoringOffer(resultSet));
			}
		} catch (SQLException e) {
			return tutoringOffers;
		}

		return tutoringOffers;
	}

	public List<TutoringOffer> getAllExcludingSpecializationAndIncludingSubject(String specialization, String subject) {
		List<TutoringOffer> tutoringOffers = new ArrayList<>();
		String query = "SELECT * FROM tutoring_offers " +
				"JOIN students ON tutoring_offers.tutor_id = students.id " +
				"JOIN student_classes ON students.student_class_id = student_classes.id " +
				"JOIN subjects ON tutoring_offers.subject_id = subjects.id " +
				"WHERE student_classes.specialization != ? AND subjects.name = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setString(1, specialization);
			statement.setString(2, subject);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				tutoringOffers.add(resultSetToTutoringOffer(resultSet));
			}
		} catch (SQLException e) {
			return tutoringOffers;
		}

		return tutoringOffers;
	}

	public List<TutoringOffer> getAllBySubject(String subject) {
		List<TutoringOffer> tutoringOffers = new ArrayList<>();
		String query = "SELECT * FROM tutoring_offers " +
				"JOIN subjects ON tutoring_offers.subject_id = subjects.id " +
				"WHERE subjects.name = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setString(1, subject);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				tutoringOffers.add(resultSetToTutoringOffer(resultSet));
			}
		} catch (SQLException e) {
			return tutoringOffers;
		}

		return tutoringOffers;
	}

	public HashMap<Integer, Integer> getAllTutoringOfferCounts() {
		String query = "SELECT tutor_id, COUNT(tutor_id) FROM tutoring_offers GROUP BY tutor_id";
		HashMap<Integer, Integer> tutoringOfferCounts = new HashMap<>();

		try {
			Statement statement = databaseConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				int tutorId = resultSet.getInt("tutor_id");
				int count = resultSet.getInt("COUNT(tutor_id)");
				tutoringOfferCounts.put(tutorId, count);
			}
		} catch (SQLException e) {
			return tutoringOfferCounts;
		}

		return tutoringOfferCounts;
	}

	public ArrayList<TutoringOffer> getAllByTutorId(int tutorId) {
		String query = "SELECT * FROM tutoring_offers WHERE tutor_id = ?";
		ArrayList<TutoringOffer> tutoringOffers = new ArrayList<>();
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, tutorId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				tutoringOffers.add(resultSetToTutoringOffer(resultSet));
			}
		} catch (SQLException e) {
			return tutoringOffers;
		}

		return tutoringOffers;
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

	public ArrayList<TutoringOffer> getByTutorId(int tutorId) {
		ArrayList<TutoringOffer> tutoringOffers = new ArrayList<>();
		String query = "SELECT * FROM tutoring_offers WHERE tutor_id = ?";
		try {
			PreparedStatement statement = databaseConnection.prepareStatement(query);
			statement.setInt(1, tutorId);
			ResultSet resultSet = statement.executeQuery();
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
