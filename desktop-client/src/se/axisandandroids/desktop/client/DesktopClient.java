package se.axisandandroids.desktop.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.service.networking.ClientSendThread;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.desktop.display.DesktopDisplayThread;
import se.axisandandroids.desktop.display.DesktopGUI;


/**
 * DesktopClient is the desktop version of our surveillance solution.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class DesktopClient {

	private Socket socket;
	private InetAddress host;	
	private int port;
	private LinkedList<Thread> threads = new LinkedList<Thread>();


	/**
	 * Create a Desktop Client instance with one display.
	 * @param host,	camera server host.
	 * @param port, camera server host.
	 */
	public DesktopClient(InetAddress host, int port) {
		this.host = host;
		this.port = port;
		connect();		
	}

	/**
	 * Set up socket.
	 */
	public void connect() {
		try {
			socket = new Socket(host, port);
		} catch (UnknownHostException e) {
			System.err.println("Unknown host.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Socket ioexception.");
			System.exit(1);
		}
		System.out.println("Connection Setup Complete: " + host +":"+port);
	}

	/**
	 * Close socket.
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		socket.close();	
		System.out.println("Client disconnected.");
		System.out.println("Interrupting threads");
		interruptThreads();		
	}
		
	/**
	 * Create the Desktop Client instace's threads, add them to list threads
	 * to await startup.
	 * @param dm, display monitor.
	 * @param gui, DesktopGUI instance.
	 */
	public void runDesktopClient(DisplayMonitor dm, DesktopGUI gui) {		

		System.out.println("** Desktop Client");

		Connection c = new Connection(socket);

		System.out.println("Creating Threads: DisplayThread, ReceiveThread, SendThread...");	

		DesktopDisplayThread disp_thread;
		if (gui == null) {
			disp_thread = new DesktopDisplayThread(dm);
		} else {
			disp_thread = new DesktopDisplayThread(dm, gui);
		}		
		DesktopReceiveThread recv_thread = new DesktopReceiveThread(c, dm, disp_thread.mailbox, gui);
		ClientSendThread send_thread = new ClientSendThread(c, dm);				

		threads.add(disp_thread);
		threads.add(recv_thread);
		threads.add(send_thread);	
	}		

	/**
	 * Start threads in list threads.
	 */
	public void startThreads() {
		System.out.println("Starting Threads: DisplayThread, ReceiveThread, SendThread...");
		for (Thread t : threads) 
			t.start();		
	}
	
	/**
	 * Interrupt the associated threads.
	 */
	public void interruptThreads() {
		System.out.println("Interrupting Threads: DisplayThread, ReceiveThread, SendThread...");
		for (Thread t : threads) {
			t.interrupt();
		} 
	}
	
	/**
	 * Main program for Desktop Client. Has capability to start multiple
	 * Desktop Client instances in a common DesktopGUI and a common DisplayMonitor.
	 * Note: the GUI does not show up until first image is received.
	 * @param args, [<camera_server_host> <port> [<camera_server_host> <port>]]
	 */
	public static void main(String[] args) {

		int nCameras = args.length/2;	

		if (nCameras == 0) {
			/* No command line arguments, assume some defaults. */
			InetAddress addr = null;
			try {
				addr = InetAddress.getByName("localhost");			
			} catch (UnknownHostException e) {
				System.err.println("Unknown host.");
				System.exit(1);
			}
			DisplayMonitor dm = new DisplayMonitor();	
			DesktopClient client0 = new DesktopClient(addr, 6000);
			client0.runDesktopClient(dm, null);
			System.exit(0);
		}

		/* Command line argument parsing */
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
		DesktopGUI gui = new DesktopGUI(dm);
		DesktopClient[] clients = new DesktopClient[nCameras]; 

		for (int i = 0; i < nCameras; ++i) {
			System.out.println("Connecting to Camera Server: " + i);
			clients[i] = new DesktopClient(addrs[i], ports[i]);
			clients[i].runDesktopClient(dm, gui);
		}

		gui.packItUp();

		for (int i = 0; i < nCameras; ++i) {
			clients[i].startThreads();
		}

		/* WAIT for threads to finish before disconnecting. */
		try {
			Thread.currentThread().join();
			for (int i = 0; i < nCameras; ++i) {
				clients[i].disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}

}
