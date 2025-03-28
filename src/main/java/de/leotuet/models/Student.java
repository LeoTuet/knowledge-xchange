package de.leotuet.models;

public class Student {
	private final int id;
	private final String firstName;
	private final String lastName;
	private final int studentClassId;

	public Student(int id, String firstName, String lastName, int studentClassId) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.studentClassId = studentClassId;
	}

	public int getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public int getStudentClassId() {
		return studentClassId;
	}

}
