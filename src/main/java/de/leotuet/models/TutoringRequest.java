package de.leotuet.models;

public class TutoringRequest {
	private final int id;
	private final int studentId;
	private final int subjectId;

	public TutoringRequest(int id, int tutorId, int subjectId) {
		this.id = id;
		this.studentId = tutorId;
		this.subjectId = subjectId;
	}

	public int getId() {
		return id;
	}

	public int getStudentId() {
		return studentId;
	}

	public int getSubjectId() {
		return subjectId;
	}

}
