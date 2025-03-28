package de.leotuet.models;

public class TutoringOfferWindow {
	private final int offerId;
	private final int windowId;

	public TutoringOfferWindow(int offerId, int windowId) {
		this.offerId = offerId;
		this.windowId = windowId;
	}

	public int getOfferId() {
		return offerId;
	}

	public int getWindowId() {
		return windowId;
	}
}
