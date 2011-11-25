package se.axisandandroids.client.display;

import java.util.PriorityQueue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import se.lth.cs.fakecamera.Axis211A;

import se.axisandandroids.networking.Protocol;

public class DisplayMonitor {
	private int disp_mode = Protocol.DISP_MODE.AUTO;
	private int sync_mode = Protocol.SYNC_MODE.AUTO;


	public DisplayMonitor() {}

	private final long DELAY_TERM_MS = 0;
	private final long DELAY_SYNCMODE_THRESHOLD_MS = 200;
	private final PriorityQueue<Long> timestamps = new PriorityQueue<Long>();
	private long showtime = 0;

	private long t0 = 0;
	private long lag = 0;


	public synchronized long syncFrames(long timestamp) throws InterruptedException {
		timestamps.offer(timestamp); // Register timestamp in queue	

		/* No old showtime exists for ANY frame, display now! */
		if (showtime <= 0) {
			t0 = System.currentTimeMillis();
			lag = DELAY_TERM_MS + t0 - timestamp;
			return lag;			
		}

		/* Calculate showtime for this thread in relation to what has been shown last. */
		long showtime = lag + timestamp;				
		long diffTime;	// Time until showtime


		/* Wait until it is:
		 * 1) The right time.										*/
		while ((diffTime = showtime - System.currentTimeMillis()) > 0) {
			wait(diffTime);		
		}

		/* 2) timestamp less than all other timestamps.				*/
		//	while (timestamp > timestamps.peek()) wait();
		
		/* SHOW TIME */
		showtime = System.currentTimeMillis();

		
		/* Time between this frame and the last shown */		
		/*
		if ((showtime_new - showtime_old) < DELAY_SYNCMODE_THRESHOLD_MS) {
			sync_mode = Protocol.SYNC_MODE.SYNC;
			//showtime_old = 0;
		} else {
			sync_mode = Protocol.SYNC_MODE.ASYNC;
		}*/
		/* Update for next Frame */
		//showtime_old = showtime_new;

		
		timestamps.remove(); // This timestamp is done
		notifyAll();
		
		/* Calculate and return delay */
		return showtime - timestamp; // The real delay
	}


	public synchronized void setDispMode(int disp_mode) {
		this.disp_mode = disp_mode;
	}

	public synchronized void setSyncMode(int sync_mode) {
		this.sync_mode = sync_mode;
	}

}
