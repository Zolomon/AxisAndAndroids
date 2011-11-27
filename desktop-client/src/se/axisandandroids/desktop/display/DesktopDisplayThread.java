package se.axisandandroids.desktop.display;

import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.display.DisplayThreadSkeleton;
import se.axisandandroids.networking.Protocol;


public class DesktopDisplayThread extends DisplayThreadSkeleton {

	private DesktopGUI gui;	
	protected final int INITIAL_BUFFER_WAIT_MS = 100;
	
	
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
		
		/* -------------------------------------  First Image is Special */
		len = mailbox.get(jpeg);
		timestamp = getTimestamp();
		int sync_mode;
		
		try {
			sync_mode = disp_monitor.getSyncMode();
			if (sync_mode == Protocol.SYNC_MODE.SYNC) {
				delay = disp_monitor.syncFrames(timestamp);
			} else if (sync_mode == Protocol.SYNC_MODE.AUTO){
				//delay = asyncFrames(timestamp);
				delay = asyncAsFastAsPossible(timestamp);	
				disp_monitor.chooseSyncMode(Thread.currentThread().getId(), delay);	
			} else { // sync_mode == Protocol.SYNC_MODE.ASYNC
				//delay = asyncFrames(timestamp);
				delay = asyncAsFastAsPossible(timestamp);	
			}						
			showFirstImage(timestamp, delay, len);
		} catch (InterruptedException e) {							// ACTION
			System.err.println("syncFrames got interrupted!");
			e.printStackTrace();
			System.out.println("Flushing mailbox");
			mailbox.flush();
		} 

		
		/* -------------------------------------- Get all other Images */
		
		while (! interrupted()) {
			len = mailbox.get(jpeg);
			timestamp = getTimestamp();
			

			try {
				sync_mode = disp_monitor.getSyncMode();
				
				if (sync_mode == Protocol.SYNC_MODE.SYNC) {
					
					delay = disp_monitor.syncFrames(timestamp);
					
				} else if (sync_mode == Protocol.SYNC_MODE.AUTO){
					
					//delay = asyncFrames(timestamp);
					delay = asyncAsFastAsPossible(timestamp);						
					disp_monitor.chooseSyncMode(Thread.currentThread().getId(), delay);
					
				} else if (sync_mode == Protocol.SYNC_MODE.ASYNC) {
					
					//delay = asyncFrames(timestamp);
					delay = asyncAsFastAsPossible(timestamp);
				
				}				
				showImage(timestamp, delay, len, sync_mode);
			} catch (InterruptedException e) {
				System.err.println("syncFrames got interrupted");
				e.printStackTrace();				
				System.out.println("Flushing mailbox");
				mailbox.flush();
			}			
		}
	}

	protected void showFirstImage(long timestamp, long delay, int len) {				
		System.out.printf("Thread: %4d\t Delay: %4d\t Sync: %4d\t Timestamp: %15d \n", this.getId(), delay, disp_monitor.getSyncMode(), timestamp);		
		gui.firstImage(this, jpeg, delay); 
	}
			
	
	private long timeforfps;
	private long countforfps;
	private double fps = 1;

	long sync_mode_old = disp_monitor.getSyncMode();
	
	protected void showImage(long timestamp, long delay, int len, int sync_mode) {
		/*
		++countforfps;		
		if (countforfps % 100 == 0) {
			fps = 1000*countforfps/(double)(System.currentTimeMillis() - timeforfps);
			timeforfps = System.currentTimeMillis();
			countforfps = 1;
		}				
		
		System.out.printf("Thread: %4d\t Delay: %4d\t Sync: %4d\t FPS: %10.2f\t Buffer Fill: %10d\n", 
						   this.getId(), delay, disp_monitor.getSyncMode(), fps, mailbox.nAvailable());
		 */
		gui.refreshImage(this, jpeg, delay);
		
		if (sync_mode != sync_mode_old) {
			gui.refreshSyncButtonText();
			sync_mode_old = sync_mode;
		}
	}

}	




