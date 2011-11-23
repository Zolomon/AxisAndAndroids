package se.axisandandroids.server;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.Frame;
import se.axisandandroids.networking.Protocol;
import se.lth.cs.fakecamera.Axis211A;

public class CameraThread extends Thread {
	
	private Axis211A myCamera;
	private byte[] jpeg;
	private CameraMonitor camera_monitor;
	private CircularBuffer mailbox;

	/**
	 * Create a CameraThread with task to Fetch images from a camera, proxy-camera
	 * or fake camera and post it to one sendthread.
	 * @param camera_monitor
	 * @param mailbox
	 */
	public CameraThread(CameraMonitor camera_monitor, CircularBuffer mailbox){
		this.camera_monitor = camera_monitor;
		this.mailbox = mailbox;
		myCamera = new Axis211A();
		jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];
	}

	public void run() {
		if (cameraConnect()) {
			while(! interrupted()) {
				int len = receiveJPEG();
				mailbox.put(new Frame(jpeg, len, true));				
			}
		}
	}

	private boolean cameraConnect(){
		if (! myCamera.connect()) {
			System.out.println("Failed to connect to camera!");
			System.exit(1);
			return false;
		} else{
			System.out.println("Camera connected!");
			return true;
		}
	}

	private int receiveJPEG(){
		int len = 0;		
		if(camera_monitor.getDislayMode() == Protocol.DISP_MODE.MOVIE) {
			len = myCamera.getJPEG(jpeg,0);
		} else if(camera_monitor.getDislayMode() == Protocol.DISP_MODE.IDLE) {
			len = myCamera.getJPEG(jpeg,0);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return len;
	}

}
