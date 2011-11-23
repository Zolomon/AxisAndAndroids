package se.axisandandroids.client;

import java.net.Socket;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.client.display.DisplayThread;
import se.axisandandroids.client.service.networking.ClientReceiveThread;
import se.axisandandroids.client.service.networking.ClientSendThread;
import se.axisandandroids.networking.Connection;

public class CameraTunnel {
	
	public Connection connection;	

	private ClientSendThread send_thread;
	private ClientReceiveThread recv_thread;
	
	public CameraTunnel(Connection c) {
		this.connection  = c;				
				
	}
	
	private void createThreads() {
		// Create the threads here !!!
		
		DisplayThread disp_thread = new DisplayThread();		
		recv_thread = new ClientReceiveThread(connection, disp_monitor, disp_thread.mailbox);
		send_thread = new ClientSendThread(connection);
	
	}
	
	public CircularBuffer getSendMailbox() {
		return send_thread.mailbox;
	}
	
}
