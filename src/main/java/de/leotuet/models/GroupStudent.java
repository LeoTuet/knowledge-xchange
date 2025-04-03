package de.leotuet.models;

public class GroupStudent {
	private final int groupId;
	private final int requestId;

	public GroupStudent(int groupId, int studentId) {
		this.groupId = groupId;
		this.requestId = studentId;
	}

	public int getGroupId() {
		return groupId;
	}

	public int getRequestId() {
		return requestId;
	}

	@Override
	public String toString() {
		return "GroupStudent{" +
				"groupId=" + groupId +
				", requestId=" + requestId +
				'}';
	}

}
