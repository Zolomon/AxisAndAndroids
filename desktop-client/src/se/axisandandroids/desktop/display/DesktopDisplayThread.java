package se.axisandandroids.desktop.display;

import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.display.DisplayThreadSkeleton;
import se.axisandandroids.networking.Protocol;


public class DesktopDisplayThread extends DisplayThreadSkeleton {

	private DesktopGUI gui;	
	protected final int BUFFERSIZE = 10;
	
	
	public DesktopDisplayThread(DisplayMonitor disp_monitor) {
		super(disp_monitor);
		gui = new DesktopGUI(disp_monitor, this);
		gui.pack();
	}
	
	public DesktopDisplayThread(DisplayMonitor disp_monitor, DesktopGUI gui) {
		super(disp_monitor);
		this.gui = gui;
		gui.registerDisplayThread(this);
	}
	
	public void close() {
		gui.deregisterDisplayThread(this);
	}
	
	@Override
	public void run() {
		int len = 0;
		long delay = -1;
		long timestamp = -1;
		
		mailbox.awaitBuffered(INITIAL_BUFFER_WAIT_MS);
		
		/* First Image is Special */
		len = mailbox.get(jpeg);
		timestamp = getTimestamp();		
		try {
			if (disp_monitor.getSyncMode() == Protocol.SYNC_MODE.SYNC) {
				delay = disp_monitor.syncFrames(timestamp);
			} else {
				//delay = asyncFrames(timestamp);
				delay = asyncAsFastAsPossible(timestamp);								
			}										
		} catch (InterruptedException e) {
			System.err.println("syncFrames got interrupted");
			e.printStackTrace();
		}		
		showFirstImage(timestamp, delay, len);

		
		/* Get all other Images */
		while (! interrupted()) {
			len = mailbox.get(jpeg);
			timestamp = getTimestamp();
			
			try {
				if (disp_monitor.getSyncMode() == Protocol.SYNC_MODE.SYNC) {
					delay = disp_monitor.syncFrames(timestamp);
				} else {
					//delay = asyncFrames(timestamp);
					delay = asyncAsFastAsPossible(timestamp);								
				}										
			} catch (InterruptedException e) {
				System.err.println("syncFrames got interrupted");
				e.printStackTrace();
			}			
			showImage(timestamp, delay, len);
		}
	}

	protected void showFirstImage(long timestamp, long delay, int len) {				
		//System.out.printf("Thread: %d\t Delay: %d\t Sync: %d\t Timestamp %d \n", this.getId(), delay, disp_monitor.getSyncMode(), timestamp);
		gui.firstImage(this, jpeg, delay); 
	}
	
	protected void showImage(long timestamp, long delay, int len) {				
		//System.out.printf("Thread: %d\t Delay: %d\t Sync: %d\t Timestamp %d \n", this.getId(), delay, disp_monitor.getSyncMode(), timestamp);		
		gui.refreshImage(this, jpeg, delay); 
	}

}	




