package se.axisandandroids.client.display;


import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.display.DisplayThreadSkeleton;


public class DesktopDisplayThread extends DisplayThreadSkeleton {

	public DesktopDisplayThread(DisplayMonitor disp_monitor) {
		super(disp_monitor);
	}

	protected void showImage(long delay, int len) {
		System.out.printf("ShowTime!!! Delay: %d", delay);
	}

}