package se.axisandandroids.client.service.networking;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.display.DisplayThread;
import se.axisandandroids.client.service.networking.ClientReceiveThread;
import se.axisandandroids.client.service.networking.ClientSendThread;
import se.axisandandroids.networking.Connection;

public class CameraTunnel {
	
	public Connection connection;	

	private DisplayMonitor disp_monitor;
	private ClientSendThread send_thread;
	private ClientReceiveThread recv_thread;
	
	public CameraTunnel(Connection c, DisplayMonitor disp_monitor) {
		this.connection  = c;				
		this.disp_monitor = disp_monitor;
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
