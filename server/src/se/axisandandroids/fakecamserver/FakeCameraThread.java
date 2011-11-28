package se.axisandandroids.fakecamserver;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.FrameBuffer;
import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.networking.Protocol;
import se.axisandandroids.server.CameraMonitor;
import se.lth.cs.fakecamera.*;


/**
 * FakeCameraThread fetches images from fakecamera and distributes them
 * to a send thread via the send threads FrameBuffer, also motion detect
 * commands is distributed but via a separate mailbox of type CircularBuffer.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class FakeCameraThread extends Thread {

	private long IDLE_PERIOD = 5000;
	private static final int FRAMESIZE = Axis211A.IMAGE_BUFFER_SIZE;
	
	private CameraMonitor camera_monitor;
	private CircularBuffer mailbox;
	private FrameBuffer frame_buffer;
		
	private byte[] jpeg = new byte[FRAMESIZE];;
	private MotionDetector md;
	private Axis211A myCamera;

	/**
	 * Create a CameraThread with task to Fetch images from a camera,
	 * proxy-camera or fake camera and post it to one send thread. 
	 * @param camera_monitor
	 * @param mailbox, send threads mailbox for commands
	 * @param frame_buffer, sed threads mailbox for images
	 * @param cam, camera
	 * @param md, motion detect
	 */
	public FakeCameraThread(CameraMonitor camera_monitor, 
							CircularBuffer mailbox,
							FrameBuffer frame_buffer,
							Axis211A cam, 
							MotionDetector md) {
		myCamera = cam;
		this.camera_monitor = camera_monitor;
		this.mailbox = mailbox;
		this.frame_buffer = frame_buffer;
		this.md = md;
	}

	public void run() {
		if (cameraConnect()) {
			while (!interrupted()) {
				while (camera_monitor.getDislayMode() == Protocol.DISP_MODE.IDLE) {
					periodReceive();
				}
				while (camera_monitor.getDislayMode() == Protocol.DISP_MODE.MOVIE) {
					int len = receiveJPEG();
					frame_buffer.put(jpeg, len);
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
		frame_buffer.put(jpeg, len);

		t += IDLE_PERIOD;
		dt = t - System.currentTimeMillis();
		try {
			if (dt > 0) {
				sleep(dt);
			}
		} catch (InterruptedException e) {
			System.err.println("Got interrupted while sleeping...");
		}
	}

	private int receiveJPEG() {
		int len = 0;
		len = myCamera.getJPEG(jpeg, 0);
		return len;
	}

	private boolean cameraConnect() {
		if (!myCamera.connect()) {
			System.err.println("Failed to connect to camera!");
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
			mailbox.put(new ModeChange(Protocol.COMMAND.DISP_MODE, Protocol.DISP_MODE.MOVIE));
			System.out.println("Motion detected!");
		} else
			System.out.println("No motion");
	}
}
