package se.axisandandroids.desktop.display;

import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.display.DisplayThreadSkeleton;


public class DesktopDisplayThread extends DisplayThreadSkeleton {

	private DesktopGUI gui;	
	protected final int BUFFERSIZE = 30;
	
	public DesktopDisplayThread(DisplayMonitor disp_monitor) {
		super(disp_monitor);
		gui = new DesktopGUI();
	}
	
	public DesktopDisplayThread(DisplayMonitor disp_monitor, DesktopGUI gui) {
		super(disp_monitor);
		this.gui = gui;
		gui.registerDisplayThread();
	}
	
	public void close() {
		gui.deregisterDisplayThread();
	}

	protected void showImage(long timestamp, long delay, int len) {				
		System.out.printf("Thread: %d\t Delay: %d\t Sync: %d\t Timestamp %d \n", this.getId(), delay, disp_monitor.getSyncMode(), timestamp);		
		gui.refreshImage(jpeg); 
	}

}	




