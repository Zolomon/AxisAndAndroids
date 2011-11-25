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

	public void runDesktopClient(DisplayMonitor dm) {		

		System.out.println("** Desktop Client");

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

		
		
		int nCameras = args.length/2;	
		
		if (nCameras == 0) {
			InetAddress addr = null;
			try {
				addr = InetAddress.getByName("localhost");			
			} catch (UnknownHostException e) {
				System.err.println("Unknown host.");
				System.exit(1);
			}
			DisplayMonitor dm = new DisplayMonitor();	
			DesktopClient client0 = new DesktopClient(addr, 6000);
			client0.runDesktopClient(dm);
			return;
		}
		
		String[] hosts = new String[nCameras];
		int[] ports = new int[nCameras];
		InetAddress[] addrs = new InetAddress[nCameras];

		for (int i = 0; i < nCameras; ++i) {
			hosts[i] = args[2*i];
			ports[i] = Integer.parseInt(args[2*i+1]);

			try {
				addrs[i] = InetAddress.getByName(hosts[i]);			
			} catch (UnknownHostException e) {
				System.err.println("Unknown host.");
				System.exit(1);
			}
		}


		/* THE FUN STARTS HERE */
		DisplayMonitor dm = new DisplayMonitor();	

		for (int i = 0; i < nCameras; ++i) {
			System.out.println("Connecting to Camera: " + i);
			DesktopClient client0 = new DesktopClient(addrs[i], ports[i]);
			client0.runDesktopClient(dm);
		}

		
		// WAIT for threads to finish before disconnecting !!!
		//client.disconnect();

	}

}
