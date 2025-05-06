package de.leotuet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

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
		try (CSVParser csvParser = CSVParser.parse(csvFile, StandardCharsets.UTF_8,
				CSVFormat.DEFAULT.builder().setDelimiter(";").build())) {
			for (CSVRecord record : csvParser) {
				records.add(record.values());
			}
			records.remove(0); // remove header
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
			if (stringSlot.isEmpty()) {
				continue;
			}

			String[] parts = stringSlot.split(":");
			String weekDay = parts[0];
			String[] time = parts[1].trim().split("-");

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
			if (stringSubject.isEmpty()) {
				continue;
			}
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
				TutoringRequest tutoringRequest = tutoringRequestRepository.createAndGet(student.getId(), subject.getId(),
						null);
				for (TimeSlot timeSlot : timeSlots) {
					tutoringRequestTimeSlotRepository.create(tutoringRequest.getId(), timeSlot.getId());
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

	private String getSpecialization(String user) {
		System.out.println("In welchem Zweig geht der " + user + "?");
		int specializationChoice = CommandLineInterface.getChoice("Technik", "Gesundheit", "Sozial", "Wirtschaft");
		return switch (specializationChoice) {
			case 1 -> "t";
			case 2 -> "g";
			case 3 -> "s";
			case 4 -> "w";
			// Default case will not be reached due to getChoice implementation
			default -> "t";
		};
	}

	private ArrayList<Subject> getSubjectsForSpecialization(String specialization, String message) {
		ArrayList<String> subjects = new ArrayList<>(List.of("Mathematik", "Deutsch", "Englisch"));
		switch (specialization) {
			case "t" -> subjects.add("Physik");
			case "g" -> subjects.add("Gesundheit");
			case "s" -> subjects.add("PäPsy");
			case "w" -> subjects.add("BWR");
		}

		ArrayList<Subject> selectedSubjects = new ArrayList<>();
		boolean running = true;

		while (!subjects.isEmpty() && running) {
			System.out.println(message);
			int subjectChoice = CommandLineInterface.getChoice(subjects.toArray(String[]::new));
			String subject = subjects.remove(subjectChoice - 1);
			selectedSubjects.add(subjectRepository.upsertAndGet(subject));

			System.out.println("Möchten Sie ein weiteres Fach hinzufügen?");
			int addMore = CommandLineInterface.getChoice("Ja", "Nein");
			if (addMore == 2) {
				running = false;
			}
		}

		return selectedSubjects;
	}

	public String getWeekDay() {
		int weekDay = CommandLineInterface.getChoice("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag");
		return switch (weekDay) {
			case 1 -> "Mo";
			case 2 -> "Di";
			case 3 -> "Mi";
			case 4 -> "Do";
			case 5 -> "Fr";
			default -> "";
		};

	}

	private int getDBTime(int hours, int minutes) {
		return hours * 100 + ((minutes / 60) * 100);
	}

	private String getDisplayTime(int dbTime) {
		int hours = dbTime / 100;
		int minutes = ((dbTime % 100) / 100) * 60;
		return hours + ":" + minutes;
	}

	private int getValidatedTimeInput(String message, int minTime, boolean isEnd) {
		boolean isValid = false;
		int hours = 0;
		int minutes = 0;

		while (!isValid) {
			String time = CommandLineInterface.getString(message);
			String[] splittedTime = time.split(":");
			if (splittedTime.length == 2) {
				try {
					hours = Integer.parseInt(splittedTime[0]);
					minutes = Integer.parseInt(splittedTime[1]);
					if (minTime > getDBTime(hours, minutes)) {
						System.out.println("Die Stunden muss nach " + getDisplayTime(minTime) + " liegen");
					} else if (hours < 7 || (!isEnd && hours > 17) || hours > 18) {
						System.out.println("Zeit außerhalb der Arbeitszeiten");
					} else if (minutes != 15 && minutes != 30 && minutes != 45 && minutes != 0) {
						System.out.println("Die Minuten müssen 0, 15, 30 oder 45 sein");
					} else if (minTime != 700 && minTime + 100 > getDBTime(hours, minutes)) {
						System.out.println("Die Zeit muss mindestens 1 Stunde nach der vorherigen Zeit liegen");
					} else {
						isValid = true;
					}
				} catch (NumberFormatException e) {
					System.out.println("Gib die Zeit im richtigen Format an (hh:mm)");
				}
			}
		}

		return getDBTime(hours, minutes);

	}

	private ArrayList<TimeSlot> getTimeSlots(String message) {
		ArrayList<TimeSlot> timeSlots = new ArrayList<>();
		boolean running = true;

		while (running) {
			System.out.println(message);
			String weekDay = getWeekDay();
			int start = getValidatedTimeInput("Ab wann hast du Zeit? (hh:mm)", 700, false);
			int end = getValidatedTimeInput("Wann hast du nicht mehr Zeit? (hh:mm)", start, true);

			while (end - start >= 100) {
				int newEnd = start + 100;
				timeSlots.add(timeSlotRepository.upsertAndGet(weekDay, start, newEnd));
				start += 25;
			}

			System.out.println("Möchten sie einen weiteren Zeitblock hinzufügen?");
			int addMore = CommandLineInterface.getChoice("Ja", "Nein");
			if (addMore == 2) {
				running = false;
			}
		}
		return timeSlots;
	}

	public int getClassYearInput() {
		int choice = CommandLineInterface.getChoice("12. Klasse", "13. Klasse");
		return switch (choice) {
			case 1 -> 12;
			case 2 -> 13;
			default -> 11;
		};
	}

	public Student selectPreferredTutor() {
		System.out.println("Hast du einen bevorzugten Tutor?");
		int choice = CommandLineInterface.getChoice("Ja", "Nein");
		if (choice == 2) {
			return null;
		}

		String lastName = CommandLineInterface.getString("Nachname:");
		String firstName = CommandLineInterface.getString("Vorname:");

		String specialization = getSpecialization("Tutor");
		int classYear = getClassYearInput();

		StudentClass studentClass = studentClassRepository.upsertAndGet(classYear, specialization);
		return studentRepository.getByAttributes(firstName, lastName, studentClass.getId());
	}

	public void manualTutorImport() {
		String lastName = CommandLineInterface.getString("Nachname:");
		String firstName = CommandLineInterface.getString("Vorname:");

		String specialization = getSpecialization("Tutor");
		int classYear = getClassYearInput();
		StudentClass studentClass = studentClassRepository.upsertAndGet(classYear, specialization);
		Student student = studentRepository.create(firstName, lastName, studentClass.getId());

		ArrayList<Subject> subjects = getSubjectsForSpecialization(specialization,
				"Welcher Fächer soll der Tutor unterrichten?");
		ArrayList<TimeSlot> timeSlots = getTimeSlots("Zu welcher Zeit willst du unterrichten?");

		for (Subject subject : subjects) {
			TutoringOffer tutoringOffer = tutoringOfferRepository.createAndGet(student.getId(), subject.getId());
			for (TimeSlot timeSlot : timeSlots) {
				tutoringOfferTimeSlotRepository.create(tutoringOffer.getId(), timeSlot.getId());
			}
		}
	}

	public void manualStudentImport() {
		String lastName = CommandLineInterface.getString("Nachname:");
		String firstName = CommandLineInterface.getString("Vorname:");

		String specialization = getSpecialization("Tutor");
		StudentClass studentClass = studentClassRepository.upsertAndGet(12, specialization);
		Student student = studentRepository.create(firstName, lastName, studentClass.getId());

		ArrayList<Subject> subjects = getSubjectsForSpecialization(specialization,
				"In welchem Fach brauchst du Nachhilfe?");
		ArrayList<TimeSlot> timeSlots = getTimeSlots("Wann hast du für deine Nachhilfestunden Zeit?");

		Student selectedTutor = selectPreferredTutor();

		for (Subject subject : subjects) {
			Integer tutor = selectedTutor != null ? selectedTutor.getId() : null;
			TutoringRequest tutoringRequest = tutoringRequestRepository.createAndGet(student.getId(), subject.getId(), tutor);
			for (TimeSlot timeSlot : timeSlots) {
				tutoringRequestTimeSlotRepository.create(tutoringRequest.getId(), timeSlot.getId());
			}
		}
	}
}
