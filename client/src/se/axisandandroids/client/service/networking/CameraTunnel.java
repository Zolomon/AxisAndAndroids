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
	private DisplayThread disp_thread;
	private ClientSendThread send_thread;
	private ClientReceiveThread recv_thread;
	
	
	public CameraTunnel(Connection c, DisplayMonitor disp_monitor) {
		this.connection  = c;				
		this.disp_monitor = disp_monitor;
		createThreads();
	}
	
	private void createThreads() {		
		System.out.println("Creating Threads: DisplayThread, ReceiveThread, SendThread...");
		disp_thread = new DisplayThread(disp_monitor);		
		recv_thread = new ClientReceiveThread(connection, disp_monitor, disp_thread.mailbox);
		send_thread = new ClientSendThread(connection);
	}
	
	private void startThreads() {		
		System.out.println("Starting Threads: DisplayThread, ReceiveThread, SendThread...");
		disp_thread.start();
		recv_thread.start();
		send_thread.start();
	}
	
	private void interruptThreads() {		
		System.out.println("Interrupting Threads: DisplayThread, ReceiveThread, SendThread...");
		disp_thread.interrupt();
		recv_thread.interrupt();
		send_thread.interrupt();
	}
		
	public CircularBuffer getSendMailbox() {
		return send_thread.mailbox;
	}
	
}
