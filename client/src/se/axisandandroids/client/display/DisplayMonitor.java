package se.axisandandroids.client.display;

import java.util.PriorityQueue;

public class DisplayMonitor extends DisplayMonitorSkeleton {
	private long t0;
	private long lag;
	private final long DELAY_TERM=10;
	
	public DisplayMonitor() {
	}

	@Override
	public synchronized long syncFrames(long timestamp)
			throws InterruptedException {
		
		long timestamp_new = timestamp; 

		/* No old showtime exists for ANY frame, display now! */
		
		if (t0 <= 0) {
			t0 = System.currentTimeMillis();
			lag = t0 - timestamp;
			lag += DELAY_TERM;
			return t0 - timestamp;			
		}
		
//		timestamps.offer(timestamp);

		/* Calculate showtime for this thread in relation to FIRST SHOWN FRAME. */
		long showtime_new = lag + timestamp;
		long diffTime; // Time to showtime_new

		/*
		 * Wait until it is: 1) The right time. 2) timestamp less than all other
		 * timestamps.
		 */
		while ((diffTime = showtime_new - System.currentTimeMillis()) > 0) {
			Thread.sleep(diffTime);
		}

		while (timestamp > timestamps.peek()) {
			wait();
		}

		/* SHOW TIME */
		timestamps.remove();
		notifyAll();

		/* Calculate and return delay */

		long delay = System.currentTimeMillis() - timestamp;
		// System.out.printf("Timestamp: %15d \t Time: %15d \t Delay: %8d \n",
		// timestamp, System.currentTimeMillis(), delay);

		chooseSyncMode(Thread.currentThread().getId(), delay);

		return delay; // The real delay
	}
}
