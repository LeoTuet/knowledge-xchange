package de.leotuet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import de.leotuet.models.Student;
import de.leotuet.models.StudentClass;
import de.leotuet.models.Subject;
import de.leotuet.models.TimeSlot;
import de.leotuet.models.TutoringOffer;
import de.leotuet.models.TutoringRequest;
import de.leotuet.repository.StudentClassRepository;
import de.leotuet.repository.StudentRepository;
import de.leotuet.repository.SubjectRepository;
import de.leotuet.repository.TimeSlotRepository;
import de.leotuet.repository.TutoringOfferRepository;
import de.leotuet.repository.TutoringOfferTimeSlotRepository;
import de.leotuet.repository.TutoringRequestRepository;
import de.leotuet.repository.TutoringRequestTimeSlotRepository;

public class Importer {
	private static final String[] USER_SELECTION = new String[] {
			"Tutor Import",
			"Schüler Import",
			"Zurück"
	};
	StudentClassRepository studentClassRepository;
	StudentRepository studentRepository;
	TimeSlotRepository timeSlotRepository;
	SubjectRepository subjectRepository;
	TutoringOfferRepository tutoringOfferRepository;
	TutoringOfferTimeSlotRepository tutoringOfferTimeSlotRepository;
	TutoringRequestRepository tutoringRequestRepository;
	TutoringRequestTimeSlotRepository tutoringRequestTimeSlotRepository;

	public Importer(Connection conn) {
		this.studentClassRepository = new StudentClassRepository(conn);
		this.studentRepository = new StudentRepository(conn);
		this.timeSlotRepository = new TimeSlotRepository(conn);
		this.subjectRepository = new SubjectRepository(conn);
		this.tutoringOfferRepository = new TutoringOfferRepository(conn);
		this.tutoringOfferTimeSlotRepository = new TutoringOfferTimeSlotRepository(conn);
		this.tutoringRequestRepository = new TutoringRequestRepository(conn);
		this.tutoringRequestTimeSlotRepository = new TutoringRequestTimeSlotRepository(conn);
	}

	public void startCSVImport() {
		boolean running = true;
		while (running) {
			int choice = CommandLineInterface.getChoice(USER_SELECTION);
			switch (choice) {
				case 1 -> csvTutorImport();
				case 2 -> csvStudentImport();
				case 3 -> {
					running = false;
				}
			}
		}
	}

	private ArrayList<String[]> loadCSV() {
		ArrayList<String[]> records = new ArrayList<>();
		String path = CommandLineInterface.getString("Gib den Pfad zur CSV-Datei an:");
		File csvFile = new File(path);
		try (CSVParser csvParser = CSVParser.parse(csvFile, StandardCharsets.UTF_8, CSVFormat.DEFAULT)) {
			for (CSVRecord record : csvParser) {
				records.add(record.values());
			}
			csvParser.close();
		} catch (IOException e) {
			System.out.println("Fehler beim Einlesen der CSV-Datei: " + e.getMessage());
		}

		return records;
	}

	private Student createStudent(String[] record) {
		String lastName = record[0];
		String firstName = record[1];
		String className = record[2];

		String specialization = className.substring(1, 2);
		int year = Integer.parseInt(className.substring(2, 4));
		StudentClass studentClass = studentClassRepository.upsertAndGet(year, specialization);
		return studentRepository.create(firstName, lastName, studentClass.getId());
	}

	private ArrayList<TimeSlot> createTimeSlots(String[] stringSlots) {
		ArrayList<TimeSlot> timeSlots = new ArrayList<>();
		for (String stringSlot : stringSlots) {
			String[] parts = stringSlot.split(":");
			String weekDay = parts[0];

			String[] time = parts[1].trim().split(":");
			int start = Integer.parseInt(time[0]);
			int end = Integer.parseInt(time[1]);

			while (end - start >= 100) {
				int newEnd = start + 100;
				timeSlots.add(timeSlotRepository.upsertAndGet(weekDay, start, newEnd));
				start += 25;
			}
		}

		return timeSlots;
	}

	private ArrayList<Subject> createSubjects(String[] stringSubjects) {
		ArrayList<Subject> subjects = new ArrayList<>();
		for (String stringSubject : stringSubjects) {
			subjects.add(subjectRepository.upsertAndGet(stringSubject));
		}
		return subjects;
	}

	private void csvTutorImport() {
		var records = loadCSV();
		if (records.isEmpty()) {
			return;
		}

		for (String[] record : records) {
			Student student = createStudent(record);

			// makes it possible to have more than two subjects / time slots
			var subjects = createSubjects(new String[] { record[3], record[4] });
			var timeSlots = createTimeSlots(new String[] { record[5], record[6] });

			for (Subject subject : subjects) {
				TutoringOffer tutoringOffer = tutoringOfferRepository.createAndGet(student.getId(), subject.getId());
				for (TimeSlot timeSlot : timeSlots) {
					tutoringOfferTimeSlotRepository.create(tutoringOffer.getId(), timeSlot.getId());
				}
			}
		}
	}

	private void csvStudentImport() {
		var records = loadCSV();
		if (records.isEmpty()) {
			return;
		}

		for (String[] record : records) {
			Student student = createStudent(record);

			// makes it possible to have more than two subjects / time slots
			var subjects = createSubjects(new String[] { record[3], record[4] });
			var timeSlots = createTimeSlots(new String[] { record[5], record[6] });

			for (Subject subject : subjects) {
				TutoringRequest tutoringOffer = tutoringRequestRepository.createAndGet(student.getId(), subject.getId(), null);
				for (TimeSlot timeSlot : timeSlots) {
					tutoringRequestTimeSlotRepository.create(tutoringOffer.getId(), timeSlot.getId());
				}
			}
		}
	}

	public void startManualImport() {
		boolean running = true;
		while (running) {
			int choice = CommandLineInterface.getChoice(USER_SELECTION);
			switch (choice) {
				case 1 -> manualTutorImport();
				case 2 -> manualStudentImport();
				case 3 -> {
					running = false;
				}
			}
		}
	}

	public void manualTutorImport() {
	}

	public void manualStudentImport() {
	}

}
