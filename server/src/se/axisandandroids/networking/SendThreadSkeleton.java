package se.axisandandroids.networking;

public class SendThreadSkeleton extends Thread {
	
	protected Connection c;
	
	public SendThreadSkeleton(Connection c) {
		this.c = c;
	}
	
	public void run() {
		while (!interrupted()) {
			perform();			
		}		
	}
	
	/* Subclass to specialize for server or client. */
	
	private void perform() {
		// 1) Wait for message with commands.
		// 2) Send commands and/or images via connection object
	}
	
}
