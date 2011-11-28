package se.axisandandroids.client.display;

import java.util.LinkedList;
import java.util.PriorityQueue;
import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.networking.Protocol;



/**
 * DisplayMonitor stores shared data between on the client side and is 
 * responsible for the synchronization of image frames from different 
 * DisplayThreads. From the delay difference of two distinct 
 * threads it also chooses the synchronization mode.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class DisplayMonitor {
	
	public final long DELAY_SYNCMODE_THRESHOLD_MS = 200;

	
	private int sync_mode = Protocol.SYNC_MODE.AUTO;	
	private int disp_mode;	// Server side, no default here


	private final LinkedList<CircularBuffer> mailboxes = new LinkedList<CircularBuffer>();

	//private final long DELAY_TERM = 20;
	//private long t0 = 0;

	private final PriorityQueue<Long> timestamps = new PriorityQueue<Long>();
	private long lag = 20;

	
	/**
	 * Construct a new DisplayMonitor.
	 */
	public DisplayMonitor() {}

	
	/**
	 * Construct a new DisplayMonitor with a target synchronization delay given
	 * by target_delay.
	 * @param target_delay, the target delay given in milliseconds.
	 */
	public DisplayMonitor(long target_delay) {
		lag = target_delay;
	}

	
	/**
	 * The method responsible to synchronize image frames between display threads.
	 * This is done according to the given timestamps to hold a target delay lag.
	 * Further synchronization is done by releasing threads only if its timestamp 
	 * is less than all other registered timestamps of threads currently in the method.	
	 * (In some iteration of this method the target lab was set to the lag of the 
	 *  first frame to synchronize. )
	 * @param timestamp
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized long syncFrames(long timestamp) throws InterruptedException {

		/*
		// No old showtime exists for ANY frame, display now!
		if (t0 <= 0) {
			t0 = System.currentTimeMillis();
			lag = t0 - timestamp;
			lag += DELAY_TERM;
			return t0 - timestamp;			
		}
		*/

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

		while (timestamp > timestamps.peek()) { // Maybe check timestamp difference too, hmmm
			wait();
		}

		/* SHOW TIME */
		timestamps.remove();
		notifyAll();

		/* Calculate and return delay */
		long delay = System.currentTimeMillis() - timestamp;						
		chooseSyncMode(Thread.currentThread().getId(), delay);
		return delay; // The real delay
	}

	
	long id_last = 0;
	long delay_last = 0;


	private NewDisplayModeCallback mNewDisplayModeCallback;
	private NewSyncModeCallback mNewSyncModeCallback;	
	
	
	/**
	 * Chooses the synchronization mode according to the delay given by
	 * parameter delay and the delay of last DisplayThread executing this
	 * method. The id parameter ensures the calculated delay distance is not
	 * between frames of the same DisplayThread. When using for example 3 
	 * DisplayThreads the synchronization mode switching may be oscillating
	 * if the delay of one Display Thread deviate from the delay of the others 
	 * above the synchronization threshold. 
	 * @param id, DisplayThread ID.
	 * @param delay, the delay of DisplayThread with ID id.
	 * @return, the synchronization mode.
	 */
	public synchronized int chooseSyncMode(long id, long delay) {	// RESOLVE !!!		
		if (sync_mode == Protocol.SYNC_MODE.AUTO || sync_mode == Protocol.SYNC_MODE.SYNC) {
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
		return sync_mode;
	}


	/**
	 * Subscribe a mailbox of a SendThread to a mail-list for forwarding of 
	 * display mode changes from AUTO to MOVIE when motion is detected by
	 * camera server.
	 * @param mailbox, a SendThread mailbox.
	 */
	public synchronized void subscribeMailbox(CircularBuffer mailbox) {
		System.out.println("SendThread mailbox subscribed");
		mailboxes.add(mailbox);
	}

	/**
	 * Unsubscribe from mailbox.
	 * @param mailbox, a SendThread mailbox.
	 */
	public synchronized void unsubscribeMailbox(CircularBuffer mailbox) {
		mailboxes.remove(mailbox);		
	}
	

	/**
	 * Post to all method for forwarding of display mode changes from AUTO to 
	 * MOVIE when motion is detected by camera server.	 
	 * @param msg, the message to post to all, eg. a ModeChange.
	 */
	public synchronized void postToAllMailboxes(Object msg) {	
		System.out.println("Posting command to all ClientSendThreads...");
		for (CircularBuffer mb : mailboxes) {
			mb.put(msg);
		}
	}

	/**
	 * Set display mode.
	 * @param disp_mode
	 */
	public synchronized void setDispMode(int disp_mode) {
		this.disp_mode = disp_mode;
		mNewDisplayModeCallback.callback(this.disp_mode);
	}

	/**
	 * Set synchronization mode.
	 * @param sync_mode
	 */
	public synchronized void setSyncMode(int sync_mode) {
		this.sync_mode = sync_mode;
		mNewSyncModeCallback.callback(this.sync_mode);
	}

	/**
	 * Get display mode. [0 == Auto, 1 == Idle, 2 == Movie]
	 * @return display mode
	 */
	public synchronized int getDispMode() { 
		return disp_mode; 
	}
	
	/**
	 * Get synchronization mode. [0 == Auto, 1 == Sync, 2 == Async]
	 * @return sync. mode
	 */
	public synchronized int getSyncMode() { 
		return sync_mode; 
	}
	
	public void setNewDisplayModeCallback(NewDisplayModeCallback callback) {
		mNewDisplayModeCallback = callback;
	}
	
	public void setNewSyncModeCallback(NewSyncModeCallback callback) {
		mNewSyncModeCallback = callback;
	}

}
