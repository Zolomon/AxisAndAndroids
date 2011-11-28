package se.axisandandroids.client.service.networking;

import java.io.IOException;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.Command;
import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.SendThreadSkeleton;


/**
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class ClientSendThread extends SendThreadSkeleton {

	private final DisplayMonitor disp_monitor;
	private final int BUFFERSIZE = 10;
	public final CircularBuffer mailbox;


	public ClientSendThread(Connection c, DisplayMonitor disp_monitor) {
		super(c);
		mailbox = new CircularBuffer(BUFFERSIZE);
		this.disp_monitor = disp_monitor;
		disp_monitor.subscribeMailbox(mailbox);
	}
	
	public void close() {
		//disp_monitor.unsubscribeMailbox(mailbox);
		interrupt();
	}

	protected void perform() {
		// 1) Wait for message with commands from buffer.
		Object command = mailbox.get();

		try {
			// 2) Send commands via connection object
			if (command instanceof ModeChange) {
				System.out.println("Dispatching Mode Change " + ((ModeChange) command).mode);
				c.sendInt(((ModeChange) command).cmd);
				c.sendInt(((ModeChange) command).mode);
			} else if (command instanceof Command) {
				c.sendInt(((Command) command).cmd);
			}
		} catch (IOException e) {					// ACTION
			System.err.println("Send fail");
			e.printStackTrace();
			System.out.println("Flushing mailbox");
			mailbox.flush();
		}
	}

	@Override
	public void interrupt() {
		// handle shit:D
		disp_monitor.unsubscribeMailbox(mailbox);
		super.interrupt();
	}
	
}
