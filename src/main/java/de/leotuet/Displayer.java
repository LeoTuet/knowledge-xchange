package de.leotuet;

import java.sql.Connection;
import java.util.List;

import de.leotuet.models.Group;
import de.leotuet.models.GroupStudent;
import de.leotuet.models.Student;
import de.leotuet.models.Subject;
import de.leotuet.models.TimeSlot;
import de.leotuet.models.TutoringOffer;
import de.leotuet.models.TutoringRequest;
import de.leotuet.repository.GroupRepository;
import de.leotuet.repository.GroupStudentRepository;
import de.leotuet.repository.StudentRepository;
import de.leotuet.repository.SubjectRepository;
import de.leotuet.repository.TimeSlotRepository;
import de.leotuet.repository.TutoringOfferRepository;
import de.leotuet.repository.TutoringRequestRepository;

public class Displayer {
	private final Connection databaseConnection;
	private final GroupRepository groupRepository;
	private final GroupStudentRepository groupStudentRepository;
	private final StudentRepository studentRepository;
	private final SubjectRepository subjectRepository;
	private final TutoringOfferRepository tutoringOfferRepository;
	private final TimeSlotRepository timeSlotRepository;
	private final TutoringRequestRepository tutoringRequestRepository;

	public Displayer(Connection conn) {
		this.databaseConnection = conn;
		this.groupRepository = new GroupRepository(databaseConnection);
		this.groupStudentRepository = new GroupStudentRepository(databaseConnection);
		this.studentRepository = new StudentRepository(conn);
		this.subjectRepository = new SubjectRepository(conn);
		this.tutoringOfferRepository = new TutoringOfferRepository(conn);
		this.timeSlotRepository = new TimeSlotRepository(conn);
		this.tutoringRequestRepository = new TutoringRequestRepository(conn);
	}

	public void displayGroups() {
		List<Group> groups = groupRepository.getAll();
		int count = 1;
		for (Group group : groups) {
			List<GroupStudent> groupStudents = groupStudentRepository.getAllByGroupId(group.getId());
			TutoringOffer offer = tutoringOfferRepository.getById(group.getOfferId());
			Student tutor = studentRepository.getById(offer.getTutorId());
			Subject subject = subjectRepository.getById(offer.getSubjectId());
			TimeSlot timeSlot = timeSlotRepository.getById(group.getTimeSlotId());

			String display = count + ". Gruppe\nSubject:\n" + subject.getName() + "\nZeitraum:\n" + timeSlot.toString()
					+ "\nTutor:\n"
					+ tutor.toString() + "\nStudents:";

			for (GroupStudent groupStudent : groupStudents) {
				TutoringRequest request = tutoringRequestRepository.getById(groupStudent.getRequestId());
				Student student = studentRepository.getById(request.getStudentId());
				display += "\n" + student.toString();
			}

			System.out.println(display);
			count++;
		}
	}

}
