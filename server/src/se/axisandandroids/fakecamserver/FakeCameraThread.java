package se.axisandandroids.fakecamserver;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.Frame;
import se.axisandandroids.networking.Protocol;
import se.axisandandroids.server.CameraMonitor;
import se.lth.cs.fakecamera.*;


public class FakeCameraThread extends Thread {

	private byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];;
	private CameraMonitor camera_monitor;
	private CircularBuffer mailbox;
	private MotionDetector md;
	private long time_intervall;

	private Axis211A myCamera;

	/**
	 * Create a CameraThread with task to Fetch images from a camera,
	 * proxy-camera or fake camera and post it to one sendthread.
	 * 
	 * @param camera_monitor
	 * @param mailbox
	 * @param md
	 */
	public FakeCameraThread(CameraMonitor camera_monitor, CircularBuffer mailbox,
			Axis211A cam, MotionDetector md) {
		myCamera = cam;
		this.camera_monitor = camera_monitor;
		this.mailbox = mailbox;
		this.md = md;

		// myCamera = new Axis211A(host, port);
		// md = new MotionDetector();
		time_intervall = 5000;
	}

	public void run() {
		if (cameraConnect()) {
			while (!interrupted()) {
				while (camera_monitor.getDislayMode() == Protocol.DISP_MODE.IDLE) {
					periodReceive();
				}
				while (camera_monitor.getDislayMode() == Protocol.DISP_MODE.MOVIE) {
					int len = receiveJPEG();
					mailbox
							.put(new Frame(jpeg, len,
									Axis211A.IMAGE_BUFFER_SIZE));
				}
				while (camera_monitor.getDislayMode() == Protocol.DISP_MODE.AUTO) {
					periodReceive();
					checkForMotion();
				}
			}
		}
	}

	private void periodReceive() {
		long t, dt;
		t = System.currentTimeMillis();

		/* Periodic Activity */
		int len = receiveJPEG();
		mailbox.put(new Frame(jpeg, len, Axis211A.IMAGE_BUFFER_SIZE));

		t += time_intervall;
		dt = t - System.currentTimeMillis();
		try {
			if (dt > 0) {
				sleep(dt);
			}
		} catch (InterruptedException e) {
			System.out.println("Got interrupted while sleeping...");
		}
	}

	private int receiveJPEG() {
		int len = 0;
		len = myCamera.getJPEG(jpeg, 0);
		return len;
	}

	private boolean cameraConnect() {
		if (!myCamera.connect()) {
			System.out.println("Failed to connect to camera!");
			System.exit(1);
			return false;
		} else {
			System.out.println("Camera connected!");
			return true;
		}
	}

	private void checkForMotion() {
		if (md.detect()) {
			camera_monitor.setDisplayMode(Protocol.DISP_MODE.MOVIE);
			System.out.println("Motion detected!");
		} else
			System.out.println("No motion");
	}
}