package se.axisandandroids.networking;

public class SendThreadSkeleton extends Thread {
	
	@SuppressWarnings("unused")
	private Connection c;
	
	public SendThreadSkeleton(Connection c) {
		this.c = c;
	}
	
	public void run() {
		while (!interrupted()) {
			// 1) Wait for message with commands.
			// 2) Send commands via connection object
		}		
	}
	

	/* Subclass to specialize for server or client. */
	
}
