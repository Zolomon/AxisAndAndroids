package se.axisandandroids.client.service.networking;

import java.net.Socket;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.client.service.networking.ClientReceiveThread;
import se.axisandandroids.networking.Connection;

public class CameraTunnel {
	
	public Connection connection;	

	public CameraTunnel(Connection c) {
		this.connection  = c;				
				
	}
	
	private void createThreads() {
		// Create the threads here !!!
		
		//	DisplayThread disp_thread = new DisplayThread();
		//FrameBuffer frame_buffer = disp_thread.getMailbox();
		//ClientReceiveThread recv_thread = new ClientReceiveThread(connection, disp_monitor, frame_buffer);
	}
	
	public CircularBuffer getSendMailbox() {
		return null;
	}
	
}
