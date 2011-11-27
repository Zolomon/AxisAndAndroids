package se.axisandandroids.desktop.client;

import java.util.PriorityQueue;

public class DesktopDisplayMonitor extends DisplayMonitorSkeleton {
	private final PriorityQueue<Long> timestamps = new PriorityQueue<Long>();

	public DesktopDisplayMonitor() {
	}

	@Override
	public synchronized long syncFrames(long timestamp)
			throws InterruptedException {

		timestamps.offer(timestamp);

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
