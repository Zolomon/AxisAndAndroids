package se.axisandandroids.client.display;

import android.graphics.BitmapFactory;

public class DisplayThread extends DisplayThreadSkeleton {
	
	
	protected final int BUFFERSIZE = 5;

	
	private NewImageCallback mNewImageCallback;

	
	public DisplayThread(DisplayMonitor disp_monitor, NewImageCallback callback) {
		super(disp_monitor);
		mNewImageCallback = callback;
	}

	public DisplayThread(DisplayMonitor disp_monitor, int id) {
		super(disp_monitor);
	}

	@Override 
	protected void showImage(long timestamp, long delay, int len, int sync_mode) {		
		mNewImageCallback.newImage(BitmapFactory.decodeByteArray(jpeg, 0, len));
		//System.out.printf("Delay: %d", delay);

	}

}
