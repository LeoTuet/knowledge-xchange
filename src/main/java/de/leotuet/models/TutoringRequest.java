package de.leotuet.models;

public class TutoringRequest {
	private final int id;
	private final int studentId;
	private final int subjectId;
	private final Integer preferredTutorId;

	public TutoringRequest(int id, int tutorId, int subjectId, int preferredTutorId) {
		this.id = id;
		this.studentId = tutorId;
		this.subjectId = subjectId;
		this.preferredTutorId = preferredTutorId;
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

	public Integer getPreferredTutorId() {
		return preferredTutorId;
	}

	@Override
	public String toString() {
		return "TutoringRequest{" +
				"id=" + id +
				", studentId=" + studentId +
				", subjectId=" + subjectId +
				", preferredTutorId=" + preferredTutorId +
				'}';
	}

}
