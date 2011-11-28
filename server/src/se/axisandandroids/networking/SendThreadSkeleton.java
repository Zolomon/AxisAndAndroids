package se.axisandandroids.networking;


/**
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class SendThreadSkeleton extends Thread {

	protected Connection c;

	public SendThreadSkeleton(Connection c) {
		this.c = c;
	}

	public void run() {
		while (!interrupted()) {
			if (c.isConnected())
				perform();
		}
	}

	/* Subclass to specialize for server or client. */

	protected void perform() {
		// 1) Wait for message with commands.
		// 2) Send commands and/or images via connection object
	}

}
