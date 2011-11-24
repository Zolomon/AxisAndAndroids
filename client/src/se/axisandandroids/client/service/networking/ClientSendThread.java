package se.axisandandroids.client.service.networking;

import java.io.IOException;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.Command;
import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.SendThreadSkeleton;

public class ClientSendThread extends SendThreadSkeleton {

	private final int BUFFERSIZE = 5;
	public CircularBuffer mailbox;

	public ClientSendThread(Connection c) {
		super(c);
		mailbox = new CircularBuffer(BUFFERSIZE);
	}

	protected void perform() {
		// 1) Wait for message with commands from buffer.
		Object command = mailbox.get();

		try {
			// 2) Send commands via connection object
			if (command instanceof Command) {
				c.sendInt(((Command) command).cmd);
			} else if (command instanceof ModeChange) {
				c.sendInt(((ModeChange) command).cmd);
				c.sendInt(((ModeChange) command).mode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
