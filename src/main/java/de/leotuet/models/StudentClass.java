package de.leotuet.models;

public class StudentClass {
	private final int id;
	private final int year;
	private final char specialization;

	public StudentClass(int id, int year, char specialization) {
		this.id = id;
		this.year = year;
		this.specialization = specialization;
	}

	public int getId() {
		return id;
	}

	public int getYear() {
		return year;
	}

	public char getSpecialization() {
		return specialization;
	}

	@Override
	public String toString() {
		return "StudentClass{" +
				"id=" + id +
				", year=" + year +
				", specialization=" + specialization +
				'}';
	}
}
