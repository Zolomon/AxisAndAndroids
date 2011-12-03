package se.axisandandroids.server;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.FrameBuffer;
import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.networking.Protocol; 
import se.lth.cs.cameraproxy.Axis211A;
import se.lth.cs.cameraproxy.MotionDetector;



/**
 * CameraThread fetches images from proxies and dispatches them to a
 * send thread given by its mailboxes for images and commands.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class CameraThread extends Thread {

	private int detectionSens 			= 3;
	private static final int FRAMESIZE 	= Axis211A.IMAGE_BUFFER_SIZE;		

	private CameraMonitor camera_monitor;
	private CircularBuffer mailbox;	
	private FrameBuffer frame_buffer;

	private byte[] jpeg = new byte[FRAMESIZE];;
	private Axis211A myCamera;
	private MotionDetector md;

	/**
	 * Create a CameraThread with task to Fetch images from a camera,
	 * proxy-camera or fake camera and post it to one sendthread.	 
	 * @param camera_monitor, camera monitor for shared data on serverside.
	 * @param mailbox, a mailbox to which motion detect commands (ModeChange(DispMode, Movie)) to client can be sent.
	 * @param cam, Axis211A camera instance from which images are fetched.
	 * @param md, motion detector.
	 */
	public CameraThread(CameraMonitor camera_monitor, 
			CircularBuffer mailbox,
			FrameBuffer frame_buffer,
			Axis211A cam, MotionDetector md) {
		myCamera = cam;
		//detectionSens = 0;  //Default for the cameras is 15, 0 is no motion and 100 is... a lot
		this.camera_monitor = camera_monitor;
		this.mailbox = mailbox;
		this.frame_buffer = frame_buffer;
		this.md = md;		
	}

	/* While the camera is connected: receive images according to the display mode */
	public void run() {
		if (cameraConnect()) {
			while (!interrupted() && !camera_monitor.getDisconnect()) {
				while (camera_monitor.getDisplayMode() == Protocol.DISP_MODE.MOVIE) {
					camera_monitor.awaitImageFetch();
					receiveImage();
				}
				while (camera_monitor.getDisplayMode() == Protocol.DISP_MODE.IDLE) {
					camera_monitor.awaitImageFetch();
					receiveImage();
				}				
				while (camera_monitor.getDisplayMode() == Protocol.DISP_MODE.AUTO) {
					camera_monitor.awaitImageFetch();
					receiveImage();
					checkForMotion();
				}
			}
		}
	}

	private void receiveImage() {		
		int len = receiveJPEG();	// ----------------------->> CORRECT TIMESTAMP HERE
		frame_buffer.put(jpeg, len);			
	}

	/**
	 * Get image from camera.
	 * @return, length of the jpeg, the image is writen in byte[] jpeg.
	 */
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
		if (md.getLevel() > detectionSens) {
			camera_monitor.setDisplayMode(Protocol.DISP_MODE.MOVIE);
			mailbox.put(new ModeChange(Protocol.COMMAND.DISP_MODE, Protocol.DISP_MODE.MOVIE));
			System.out.println("Motion detected!");
		} else {
			System.out.println("No motion");
		}
	}
	
	public void interrupt() {
		camera_monitor.setDisconnect(true);
		super.interrupt();
	}
}
