package se.axisandandroids.server;

import java.util.LinkedList;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.ClockSync;
import se.axisandandroids.buffer.Command;
import se.axisandandroids.networking.Protocol;

/**
 * Camera monitor is responsible for syncing of shared data on the server side.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class CameraMonitor {

	private final long IDLE_PERIOD = 5000;
	private long t;
	private int display_mode = -1;

	public CameraMonitor() {
		this.setDisplayMode(Protocol.DISP_MODE.AUTO);
		//this.setDisplayMode(Protocol.DISP_MODE.MOVIE);
	}

	/**
	 * Setting a new display mode, aka. changing the behaviour of the cameraThread.
	 * @param display_mode An integer that specifies the display mode.
	 * @return true if change is successful, false otherwise.
	 */
	public synchronized void setDisplayMode(int display_mode) {	
		if (display_mode != this.display_mode) {
			System.out.println("New display mode: " + display_mode);		
			this.display_mode = display_mode;
			notifyAll();
			if (display_mode == Protocol.DISP_MODE.AUTO ||
					display_mode == Protocol.DISP_MODE.IDLE) {
				t = System.currentTimeMillis();
			}
		}
	}


	/**
	 * 
	 * @return an integer with the display mode.
	 */
	public synchronized int getDislayMode() {
		return display_mode;
	}

	public synchronized void awaitImageFetch() {
		long dt;		
		t += IDLE_PERIOD;
		try {
			while ((dt = t - System.currentTimeMillis()) > 0
					&& display_mode != Protocol.DISP_MODE.MOVIE) {
				wait(dt);
			}
		} catch (InterruptedException e) {
			System.err.println("Got interrupted while waiting...");
		}
	}



	/* Clock Sync Experiments */
	long sendTime;
	LinkedList<Long> corrections = new LinkedList<Long>();

	int sentmessages = 0;
	int recvmessages = 0;

	public synchronized void sync_clocks(CircularBuffer mailbox) {
		sendTime = System.currentTimeMillis();
		System.out.printf("%d) CLOCK SYNC Put in mailbox.\n", sentmessages);
		mailbox.put(new ClockSync());		
		++sentmessages;
		notifyAll();
	}

	public synchronized void sync_clocks(long time, long recvTime) {
		System.out.printf("%d) CLOCK SYNC got time: %d.\n", recvmessages, time);
		long estimatedTime = sendTime + (recvTime - sendTime)/2;
		corrections.offer(estimatedTime - time);		
		++recvmessages;
		notifyAll();
	}

	public synchronized long getCorrection() {
		/*
		while (sentmessages > recvmessages) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		*/
		if (! corrections.isEmpty()) {
			long sum = 0;
			int count = 0;
			for (Long c : corrections) {
				sum += c;
				++count;
			}
			//for (int i = 0; i < count; ++i) corrections.poll();
			return sum/count;		
		}
		return 0;
	}

}
