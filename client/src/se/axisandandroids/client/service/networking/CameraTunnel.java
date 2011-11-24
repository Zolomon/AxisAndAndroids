package se.axisandandroids.client.service.networking;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.display.DisplayThread;
import se.axisandandroids.client.display.NewImageCallback;
import se.axisandandroids.client.display.Panel;
import se.axisandandroids.client.service.networking.ClientReceiveThread;
import se.axisandandroids.client.service.networking.ClientSendThread;
import se.axisandandroids.networking.Connection;

public class CameraTunnel {
	
	public Connection connection;		
	private DisplayMonitor disp_monitor;
	private DisplayThread disp_thread;
	private ClientSendThread send_thread;
	private ClientReceiveThread recv_thread;
	private NewImageCallback mNewImageCallback;
	private int id;
	
	public CameraTunnel(Connection c, Panel p, DisplayMonitor disp_monitor, int id) {
		this.id = id;
		this.connection  = c;				
		this.disp_monitor = disp_monitor;
		this.mNewImageCallback = p.getNewImageCallback();		
		createThreads();
	}
	
	
	/**
	 * For DESKTOP client which has no Panel.
	 * @param c
	 * @param disp_monitor
	 * @param id
	 */
	public CameraTunnel(Connection c, DisplayMonitor disp_monitor, int id) {
		this.id = id;
		this.connection  = c;				
		this.disp_monitor = disp_monitor;
		createThreads();
	}
			
	private void createThreads() {		
		System.out.println("Creating Threads: DisplayThread, ReceiveThread, SendThread...");
		disp_thread = new DisplayThread(disp_monitor, mNewImageCallback);		
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
