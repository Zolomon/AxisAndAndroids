package se.axisandandroids.client.display;

import java.util.PriorityQueue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import se.lth.cs.fakecamera.Axis211A;

import se.axisandandroids.networking.Protocol;

public class DisplayMonitor {
	private int disp_mode = Protocol.DISP_MODE.AUTO;
	private int sync_mode = Protocol.SYNC_MODE.AUTO;


	public DisplayMonitor() {
	}


	private final long DELAY_SYNCMODE_THRESHOLD_MS = 200;
	private final PriorityQueue<Long> timestamps = new PriorityQueue<Long>();
	private long showtime_old = 0;
	private long timestamp_old = 0;	


	public synchronized long syncFrames(long timestamp) throws InterruptedException {

		/* No old showtime exists for ANY frame, display now! */
		if (showtime_old <= 0) return System.currentTimeMillis() - timestamp;

		/* Calculate showtime for this thread in relation to what has been shown last. */
		long showtime_new = showtime_old + (timestamp - timestamp_old);				
		long diffTime;	// Time to showtime_new

		if (sync_mode == Protocol.SYNC_MODE.ASYNC) {

			/* Wait until it is:
			 * 1) The right time.										*/
			while ((diffTime = showtime_new - System.currentTimeMillis()) > 0) {
				wait(diffTime);		
			}

		} else if (sync_mode == Protocol.SYNC_MODE.SYNC) {

			timestamps.offer(timestamp); // Register timestamp in queue	

			/* Wait until it is:
			 * 1) The right time.
			 * 2) timestamp less than all other timestamps.				*/
			while ((diffTime = showtime_new - System.currentTimeMillis()) > 0 
					&& timestamp > timestamps.peek()) {
				wait(diffTime);		
			}

			timestamps.remove(); // This timestamp is done

			notifyAll();
		}

		/* SHOW TIME */
		
		/* Time between this frame and the last shown */
		long time_between_frames = System.currentTimeMillis() - showtime_old;
		
		if (time_between_frames < DELAY_SYNCMODE_THRESHOLD_MS) {
			sync_mode = Protocol.SYNC_MODE.SYNC;
		} else {
			sync_mode = Protocol.SYNC_MODE.ASYNC;
		}
										
		/* Update for next Frame */
		timestamp_old = timestamp;
		showtime_old = showtime_new;
		
		/* Calculate delay and determine sync mode */
		long delay = System.currentTimeMillis() - timestamp; 	// The real delay		
		return delay; 											// Return delay to show
	}

	public synchronized void setDispMode(int disp_mode) {
		this.disp_mode = disp_mode;
	}

	public synchronized void setSyncMode(int sync_mode) {
		this.sync_mode = sync_mode;
	}

}
