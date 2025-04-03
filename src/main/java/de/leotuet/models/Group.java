package de.leotuet.models;

public class Group {
	private final int id;
	private final String offerId;
	private final int timeSlotId;

	public Group(int id, String offerId, int timeSlotId) {
		this.id = id;
		this.offerId = offerId;
		this.timeSlotId = timeSlotId;
	}

	public int getId() {
		return id;
	}

	public String getOfferId() {
		return offerId;
	}

	public int getTimeSlotId() {
		return timeSlotId;
	}

	@Override
	public String toString() {
		return "Group{" +
				"id=" + id +
				", offerId='" + offerId + '\'' +
				", timeSlotId=" + timeSlotId +
				'}';
	}

}
