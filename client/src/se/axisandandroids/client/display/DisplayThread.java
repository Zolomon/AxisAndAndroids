package se.axisandandroids.client.display;

import android.graphics.BitmapFactory;


/**
 * Display thread gets images in its FrameBuffer and gets them synchronized
 * and lets the GUI show them.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class DisplayThread extends DisplayThreadSkeleton {
	
	private NewImageCallback mNewImageCallback;
	
	/**
	 * Create a new DisplayThread.
	 * @param disp_monitor, display monitor to sync with other display threads.
	 * @param callback, callback for rendering of image in the GUI.
	 */
	public DisplayThread(DisplayMonitor disp_monitor, NewImageCallback callback) {
		super(disp_monitor);
		mNewImageCallback = callback;
	}

	private static final int NUM_FPS_SAMPLES = 64;
	private float[] fpsSAMPLES = new float[NUM_FPS_SAMPLES];
	private int currentSample = 0;
	private long oldTime;

	@Override 
	protected void showImage(long timestamp, long delay, int len, int sync_mode) {
		long newTime = System.currentTimeMillis();
		long dt = newTime - oldTime;
		float fps = 1.0f / dt;
		oldTime = newTime;
		
		mNewImageCallback.newImage(BitmapFactory.decodeByteArray(jpeg, 0, len));
		System.out.printf("Thread: %4d\t Delay: %4d\t Sync: %4d\t FPS: %10.2f\t Buffer Fill: %10d\n", 
				   this.getId(), delay, disp_monitor.getSyncMode(), fps, mailbox.nAvailable());
	}

}
