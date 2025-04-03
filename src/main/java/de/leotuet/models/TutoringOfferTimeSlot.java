package de.leotuet.models;

public class TutoringOfferTimeSlot {
	private final int offerId;
	private final int timeSlotId;

	public TutoringOfferTimeSlot(int offerId, int windowId) {
		this.offerId = offerId;
		this.timeSlotId = windowId;
	}

	public int getOfferId() {
		return offerId;
	}

	public int getTimeSlotId() {
		return timeSlotId;
	}

	@Override
	public String toString() {
		return "TutoringOfferTimeSlot{" +
				"offerId=" + offerId +
				", timeSlotId=" + timeSlotId +
				'}';
	}
}
