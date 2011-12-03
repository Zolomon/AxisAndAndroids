package se.axisandandroids.client.service.networking;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.ClockSync;
import se.axisandandroids.buffer.Command;
import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.Protocol;
import se.axisandandroids.networking.SendThreadSkeleton;


/**
 * SendThread for client, responsible for sending commands to one camera 
 * server.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class ClientSendThread extends SendThreadSkeleton {

	private final DisplayMonitor disp_monitor;
	private final int BUFFERSIZE = 10;
	public final CircularBuffer mailbox;


	/**
	 * Create client send thread. The send thread subscribes it mailbox to
	 * the display monitor in order to receive post to all meassages.
	 * @param c, Conneciton to camera server.
	 * @param disp_monitor, display monitor.
	 */
	public ClientSendThread(Connection c, DisplayMonitor disp_monitor) {
		super(c);
		mailbox = new CircularBuffer(BUFFERSIZE);
		this.disp_monitor = disp_monitor;
		disp_monitor.subscribeMailbox(mailbox);
	}	

	public void run() {

		disp_monitor.awaitConnected();
		mailbox.put(new Command(Protocol.COMMAND.CONNECTED));

		while (!interrupted() && !disp_monitor.getDisconnect()) {
			perform();
		}
		interrupt();
	}

	protected void perform() {
		// 1) Wait for message with commands from buffer.
		Object command = mailbox.get();

		// 2) Send commands via connection object
		if (command instanceof ModeChange) {
			System.out.println("Dispatching Mode Change " + ((ModeChange) command).mode);
			c.sendInt(((ModeChange) command).cmd);
			c.sendInt(((ModeChange) command).mode);
		} else if (command instanceof ClockSync) {
			System.out.println("Client Sending clock sync: " + ((ClockSync) command).time);			
			c.sendInt(((ClockSync) command).cmd);
			c.sendBytes(((ClockSync) command).getBytes(), 0, 6);
		} else if (command instanceof Command) {
			c.sendInt(((Command) command).cmd);
		}		
	}


	public void close() {
		//disp_monitor.unsubscribeMailbox(mailbox);
		interrupt();
	}

	@Override
	public void interrupt() {
		// handle shit:D
		disp_monitor.unsubscribeMailbox(mailbox);
		super.interrupt();
	}

}
