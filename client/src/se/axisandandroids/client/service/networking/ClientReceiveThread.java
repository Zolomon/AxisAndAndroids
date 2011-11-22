package se.axisandandroids.client.service.networking;

import java.io.IOException;

import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.ReceiveThreadSkeleton;
import se.lth.cs.fakecamera.Axis211A;

public class ClientReceiveThread extends ReceiveThreadSkeleton {

	private byte[] img = new byte[Axis211A.IMAGE_BUFFER_SIZE];

	public ClientReceiveThread(Connection c) {
		super(c);
	}

	protected void handleImage() {
		int len = 0;
		try {
			len = c.recvImage(img);
		} catch (IOException e) {
			System.err.println("Failed to get image. Skipping this.");
			e.printStackTrace();
			return;
		}

		// Post img to displayThreads buffer

	}

	protected void handleSyncMode() {
		int sync_mode = -1;
		try {
			sync_mode = c.recvSyncMode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	protected void handleDispMode() {
		int disp_mode = -1;
		try {
			disp_mode = c.recvDisplayMode();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void handleConnected() {

	}

}
