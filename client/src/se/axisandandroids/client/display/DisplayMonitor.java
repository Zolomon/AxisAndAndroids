package se.axisandandroids.client.display;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.networking.Protocol;



public class DisplayMonitor {
	
	private int disp_mode = Protocol.DISP_MODE.AUTO;
	private int sync_mode = Protocol.SYNC_MODE.AUTO;
	
	private final LinkedList<CircularBuffer> mailboxes = new LinkedList<CircularBuffer>();

	public final long DELAY_SYNCMODE_THRESHOLD_MS = 200;
	private final long DELAY_TERM = 0;
//	private final long MAXERROR = 200;

	private final PriorityQueue<Long> timestamps = new PriorityQueue<Long>();
	private long t0 = 0;
	private long lag = 0;
	

	public DisplayMonitor() {}


	

	
	public synchronized void putTimestamp(long timestamp) {
		timestamps.offer(timestamp);
	}
	
	public synchronized long pollTimestamp() {
		return timestamps.poll();
	}
		
	public synchronized long syncFrames(long timestamp) throws InterruptedException {
		
		/* No old showtime exists for ANY frame, display now! */
		if (t0 <= 0) {
			t0 = System.currentTimeMillis();
			lag = t0 - timestamp;
			lag += DELAY_TERM;
			return t0 - timestamp;	
		}

		timestamps.offer(timestamp);

		/* Calculate showtime for this thread in relation to FIRST SHOWN FRAME. */
		long showtime_new = lag + timestamp;				
		long diffTime;	// Time to showtime_new
	
		/* Wait until it is:
		 * 1) The right time.
		 * 2) timestamp less than all other timestamps.				*/
		
		while ((diffTime = showtime_new - System.currentTimeMillis()) > 0) {
			Thread.sleep(diffTime);		
		} 
		
		while (timestamp > timestamps.peek()) {
			wait();
		}
				
		
		
		/* SHOW TIME */
		showtime_new = System.currentTimeMillis();

		/*
		if (Math.abs(showtime_new - (lag + timestamp)) > MAXERROR) {
			System.err.println("Error got a bit big increasing the lag.");
			lag += DELAY_TERM;
		}
		*/

		timestamps.remove();
		notifyAll();

		/* Calculate and return delay */
		long delay = showtime_new - timestamp;						
		chooseSyncMode(Thread.currentThread().getId(), delay);
	

		return delay; // The real delay
	}
		
	
	long id_last = 0;
	long delay_last = 0;
	
	public synchronized void chooseSyncMode(Long id, Long delay) {							
		if (id != id_last) {
			long dist = Math.abs(delay_last - delay);
			if (dist >= DELAY_SYNCMODE_THRESHOLD_MS) {
				sync_mode = Protocol.SYNC_MODE.AUTO;
			} else {
				sync_mode = Protocol.SYNC_MODE.SYNC;
			}
			id_last = Thread.currentThread().getId();
			delay_last = delay;		
		}		
	}
	
	
	
	public synchronized void subscribeMailbox(CircularBuffer mailbox) {
		mailboxes.add(mailbox);
	}
	
	public synchronized void unsubscribeMailbox(CircularBuffer mailbox) {
		mailboxes.remove(mailbox);		
	}
	
	public synchronized void postToAllMailboxes(Object msg) {	
		System.out.println("Posting command to all ClientSendThreads.");
		for (CircularBuffer mb : mailboxes) {
			mb.put(msg);
		}
	}

	public synchronized void setDispMode(int disp_mode) {
		this.disp_mode = disp_mode;
	}

	public synchronized void setSyncMode(int sync_mode) {
		this.sync_mode = sync_mode;
	}
	
	public synchronized int getDispMode() { return disp_mode; }
	public synchronized int getSyncMode() { return sync_mode; }

}
