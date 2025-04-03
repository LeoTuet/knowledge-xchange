package de.leotuet.models;

public class TutoringRequestTimeSlot {
	private final int requestId;
	private final int timeSlotId;

	public TutoringRequestTimeSlot(int requestId, int windowId) {
		this.requestId = requestId;
		this.timeSlotId = windowId;
	}

	public int getRequestId() {
		return requestId;
	}

	public int getTimeSlotId() {
		return timeSlotId;
	}

	@Override
	public String toString() {
		return "TutoringRequestTimeSlot{" +
				"requestId=" + requestId +
				", timeSlotId=" + timeSlotId +
				'}';
	}
}
