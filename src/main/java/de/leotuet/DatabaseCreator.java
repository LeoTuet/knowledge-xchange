package de.leotuet;

import java.sql.Connection;
import java.sql.SQLException;

import de.leotuet.repository.GroupRepository;
import de.leotuet.repository.GroupStudentRepository;
import de.leotuet.repository.StudentClassRepository;
import de.leotuet.repository.StudentRepository;
import de.leotuet.repository.SubjectRepository;
import de.leotuet.repository.TimeSlotRepository;
import de.leotuet.repository.TutoringOfferRepository;
import de.leotuet.repository.TutoringOfferTimeSlotRepository;
import de.leotuet.repository.TutoringRequestRepository;
import de.leotuet.repository.TutoringRequestTimeSlotRepository;

public class DatabaseCreator {
	public static void createTables(Connection conn) throws SQLException {
		StudentClassRepository.createTable(conn);
		StudentRepository.createTable(conn);
		SubjectRepository.createTable(conn);
		TimeSlotRepository.createTable(conn);

		TutoringOfferRepository.createTable(conn);
		TutoringRequestRepository.createTable(conn);

		TutoringOfferTimeSlotRepository.createTable(conn);
		TutoringRequestTimeSlotRepository.createTable(conn);

		GroupRepository.createTable(conn);
		GroupStudentRepository.createTable(conn);

	}
}
