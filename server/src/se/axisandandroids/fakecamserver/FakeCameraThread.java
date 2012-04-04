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
			while (!interrupted() && !camera_monitor.getDisconnect()) {								
				while (camera_monitor.getDisplayMode() == Protocol.DISP_MODE.IDLE) {
					camera_monitor.awaitImageFetch();
					receiveJPEG();
				}
				while (camera_monitor.getDisplayMode() == Protocol.DISP_MODE.MOVIE) {
					//camera_monitor.sync_clocks(mailbox);
					//System.out.println("Correction: " + camera_monitor.getCorrection());						
					receiveJPEG();
				}
				while (camera_monitor.getDisplayMode() == Protocol.DISP_MODE.AUTO) {
					camera_monitor.awaitImageFetch();
					receiveJPEG();
					checkForMotion();
				}								
			}
		}
	}

	private void receiveJPEG() {
		int len = 0;
		len = myCamera.getJPEG(jpeg, 0);
		frame_buffer.put(jpeg, len);
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
