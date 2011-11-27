package se.axisandandroids.client.display;

import java.util.LinkedList;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.networking.Protocol;

public class DisplayMonitorSkeleton {

	protected int disp_mode;
	protected int sync_mode = Protocol.SYNC_MODE.AUTO;
	protected final LinkedList<CircularBuffer> mailboxes = new LinkedList<CircularBuffer>();
	protected final long DELAY_SYNCMODE_THRESHOLD_MS = 100;
	protected long id_last = 0;
	protected long delay_last = 0;

	public DisplayMonitorSkeleton() {
	}

	/**
	 * To be overridden by a client on either Android or Desktop
	 * 
	 * @param timestamp
	 * @return
	 * @throws InterruptedException
	 */
	protected synchronized long syncFrames(long timestamp)
			throws InterruptedException {
	}

	protected synchronized int chooseSyncMode(long id, long delay) {
		if (sync_mode == Protocol.SYNC_MODE.AUTO
				|| sync_mode == Protocol.SYNC_MODE.SYNC) {
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

	protected synchronized void subscribeMailbox(CircularBuffer mailbox) {
		System.out.println("SendThread mailbox subscribed");
		mailboxes.add(mailbox);
	}

	protected synchronized void unsubscribeMailbox(CircularBuffer mailbox) {
		mailboxes.remove(mailbox);
	}

	protected synchronized void postToAllMailboxes(Object msg) {
		System.out.println("Posting command to all ClientSendThreads...");
		for (CircularBuffer mb : mailboxes) {
			mb.put(msg);
		}
	}

	protected synchronized void setDispMode(int disp_mode) {
		this.disp_mode = disp_mode;
	}

	protected synchronized void setSyncMode(int sync_mode) {
		this.sync_mode = sync_mode;
	}

	protected synchronized int getDispMode() {
		return disp_mode;
	}

	protected synchronized int getSyncMode() {
		return sync_mode;
	}

}
