package se.axisandandroids.desktop.client;



import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.service.networking.ClientReceiveThread;
import se.axisandandroids.client.service.networking.ClientSendThread;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.desktop.display.DesktopDisplayThread;


public class DesktopClient {

	private Socket socket;
	private InetAddress host;	
	private final static int default_port = 6000;
	private int port;

	public DesktopClient(InetAddress host, int port) {
		this.host = host;
		this.port = port;
		connect();
	}

	public void connect() {
		try {
			socket = new Socket(host, port);
		} catch (UnknownHostException e) {
			System.err.println("Unknown host.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("io-exception.");
			System.exit(1);
		}
		System.out.println("Connection Setup Complete: " + host +":"+port);
	}

	public void disconnect() throws IOException {
		socket.close();
		System.out.println("Client disconnected.");
	}

	public void runDesktopClient() {		

		System.out.println("** Desktop Client");

		DisplayMonitor dm = new DisplayMonitor();	
		
		
		// Display 0
		int id = 0;
		Connection c = new Connection(socket);

		System.out.println("Creating Threads: DisplayThread, ReceiveThread, SendThread...");
		DesktopDisplayThread disp_thread = new DesktopDisplayThread(dm);		
		ClientReceiveThread recv_thread = new ClientReceiveThread(c, dm, disp_thread.mailbox);
		ClientSendThread send_thread = new ClientSendThread(c);
		
		System.out.println("Starting Threads: DisplayThread, ReceiveThread, SendThread...");
		disp_thread.start();
		recv_thread.start();
		send_thread.start();
		
	}		


	public static void main(String[] args) {
		InetAddress addr = null;
		int port = default_port;

		try {
			addr = InetAddress.getByName("localhost");
			if (args.length >= 1) {
				addr = InetAddress.getByName(args[0]);
			}
		} catch (UnknownHostException e) {
			System.err.println("Unknown host.");
			System.exit(1);
		}

		if (args.length >= 2) {
			port = Integer.parseInt(args[1]);
		}

		DesktopClient client = new DesktopClient(addr, port);

		client.runDesktopClient();
		
		
		// WAIT for threads to finish before disconnecting !!!
		//client.disconnect();


	}

}
