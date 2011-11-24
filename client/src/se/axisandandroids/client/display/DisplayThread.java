package se.axisandandroids.client.display;

public class DisplayThread extends DisplayThreadSkeleton {
			
	public DisplayThread(DisplayMonitor disp_monitor, int id) {
		super(disp_monitor, id);
	}
	
	protected void showImage(long delay, int len) {
		System.out.printf("ShowTime!!! Delay: %d", delay);
	}
	
}
