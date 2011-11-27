package se.axisandandroids.client.display;

import android.graphics.BitmapFactory;

public class DisplayThread extends DisplayThreadSkeleton {
	
	private NewImageCallback mNewImageCallback;
	
	public DisplayThread(DisplayMonitor disp_monitor, NewImageCallback callback) {
		super(disp_monitor);
		mNewImageCallback = callback;
	}

	static final int NUM_FPS_SAMPLES = 64;
	float[] fpsSAMPLES = new float[NUM_FPS_SAMPLES];
	int currentSample = 0;

	private long oldTime;
	
	public DisplayThread(DisplayMonitor disp_monitor, int id) {
		super(disp_monitor);
	}

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
