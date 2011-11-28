package se.axisandandroids.server;

import se.axisandandroids.networking.Protocol;

/**
 * Camera monitor is responsible for syncing of shared data on the server side.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class CameraMonitor {

	private int display_mode;

	public CameraMonitor() {
		display_mode = Protocol.DISP_MODE.AUTO;
		//display_mode = Protocol.DISP_MODE.MOVIE;
	}
	/**
	 * Setting a new display mode, aka. changing the behaviour of the cameraThread.
	 * @param display_mode An integer that specifies the display mode.
	 * @return true if change is successful, false otherwise.
	 */
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
	/**
	 * 
	 * @return an integer with the display mode.
	 */
	public synchronized int getDislayMode() {
		return display_mode;
	}

}
