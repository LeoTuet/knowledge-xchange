package de.leotuet.models;

public class TutoringOffer {
	private final int id;
	private final int tutorId;
	private final int subjectId;

	public TutoringOffer(int id, int tutorId, int subjectId) {
		this.id = id;
		this.tutorId = tutorId;
		this.subjectId = subjectId;
	}

	public int getId() {
		return id;
	}

	public int getTutorId() {
		return tutorId;
	}

	public int getSubjectId() {
		return subjectId;
	}
}
