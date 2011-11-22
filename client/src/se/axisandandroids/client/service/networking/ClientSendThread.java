package se.axisandandroids.client.service.networking;

import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.SendThreadSkeleton;

public class ClientSendThread extends SendThreadSkeleton {

	public ClientSendThread(Connection c) {
		super(c);
	}
	
	protected void perform() {
		// 1) Wait for message with commands from buffer.
		// 2) Send commands via connection object
	}
	
}
