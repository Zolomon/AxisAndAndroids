package se.axisandandroids.server;

import se.axisandandroids.networking.Protocol;

public class CameraMonitor {

	private int display_mode;

	public CameraMonitor() {
		display_mode = Protocol.DISP_MODE.AUTO;
		//display_mode = Protocol.DISP_MODE.MOVIE;
	}

	public synchronized boolean setDisplayMode(int display_mode) {
		if (display_mode != Protocol.DISP_MODE.MOVIE
				&& display_mode != Protocol.DISP_MODE.IDLE
				&& display_mode != Protocol.DISP_MODE.AUTO) {
			System.out.println("Invalid Display mode!");
			return false;
		} else {
			this.display_mode = display_mode;
			System.out.println("New display mode: " + display_mode);
			return true;
		}
	}

	public synchronized int getDislayMode() {
		return display_mode;
	}

}
