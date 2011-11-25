package se.axisandandroids.client.display;

import android.graphics.BitmapFactory;

public class DisplayThread extends DisplayThreadSkeleton {
	
	private NewImageCallback mNewImageCallback;

	
	public DisplayThread(DisplayMonitor disp_monitor, NewImageCallback callback) {
		super(disp_monitor);
		mNewImageCallback = callback;
	}

	public DisplayThread(DisplayMonitor disp_monitor, int id) {
		super(disp_monitor);
	}

	@Override 
	protected void showImage(long delay, int len) {		
		mNewImageCallback.newImage(BitmapFactory.decodeByteArray(jpeg, 0, len));
		//System.out.printf("Delay: %d", delay);

	}

}
