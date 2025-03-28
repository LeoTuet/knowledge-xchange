package de.leotuet.models;

public class TutoringRequestWindow {
	private final int requestId;
	private final int windowId;

	public TutoringRequestWindow(int requestId, int windowId) {
		this.requestId = requestId;
		this.windowId = windowId;
	}

	public int getRequestId() {
		return requestId;
	}

	public int getWindowId() {
		return windowId;
	}
}
