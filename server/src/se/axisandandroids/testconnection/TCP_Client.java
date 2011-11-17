package se.axisandandroids.testconnection;

/* ---------------------------------------------------
 * Simple TCP client for sending lines of user input 
 * to some server.
 * 
 * Based on this tutorial:
 * 		http://download.oracle.com/javase/tutorial/...
 * 				networking/sockets/readingWriting.html
 * --------------------------------------------------- */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.Protocol;

public class TCP_Client {
	
	
	private Socket socket;
	private InetAddress host;	
	private final static int default_port = 6000;
	private int port;

	PrintWriter out = null;
	BufferedReader in = null;

	public TCP_Client(InetAddress host, int port) {
		this.host = host;
		this.port = port;
		connect();
	}

	public void connect() {
		try {
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Unknown host.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("io-exception.");
			System.exit(1);
		}

		System.out.println("Connection Setup Complete");
	}

	public void disconnect() throws IOException {
		out.close();
		in.close();
		socket.close();
	}

	public void userinput_echo() throws IOException {
		System.out.println("Scream to the ether and it will answer:");

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(
				System.in));
		String userInput;
		while ((userInput = stdIn.readLine()) != null) {
			out.println(userInput);
			System.out.println(in.readLine());
		}
		stdIn.close();
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

		TCP_Client tcpclient = new TCP_Client(addr, port);

		try {
			// tcpclient.userinput_echo();
			// tcpclient.connection_test();

			tcpclient.testFakeCam();			
			tcpclient.disconnect();
		} catch (IOException e) {
			System.err.println("io-exception");
			System.exit(1);
		}
	}
	
	
	public void connection_test() throws IOException {		
		System.out.println("Connection Test");

		Connection con = new Connection(socket);

		// Test sendInt()
		System.out.println("\n** Sending int...");
		int nbr = 983745;
		System.out.printf("Sending int: %d\n", nbr);
		con.sendInt(nbr);

		// Test recvInt()
		System.out.println("\n** Receiving int...");
		nbr = con.recvInt();
		System.out.printf("Got int: %d\n", nbr);	

		// Test sendSyncMode
		System.out.println("\n** Sending SyncMode...");
		con.sendSyncMode(Protocol.SYNC_MODE.AUTO);

		// Test sendDisplayMode
		System.out.println("\n** Sending DisplayMode...");
		con.sendDisplayMode(Protocol.DISP_MODE.AUTO);

		// Test recvImage
		byte[] c = { 12,43,34,120,21,32,100,34 };			
		System.out.println("\n** Receiving Image...");
		int cmd = con.recvInt();
		assert(cmd == Protocol.COMMAND.IMAGE);
		System.out.println("Command: " + cmd);			
		byte[] b = con.recvImage();		
		System.out.printf("Length: %d\n", b.length);
		for (int i = 0; i < b.length; ++i) {
			System.out.printf("%d ", b[i]);
			assert(b[i] == c[i]);
		}
		System.out.println();

		// Test sendImage
		System.out.println("\n** Sending Image...");
		con.sendImage(c,0,c.length);
	}
	
	public void testFakeCam() throws IOException {
		System.out.println("**Test FakeCam");
		
		Connection con = new Connection(socket);
		
		int cmd = con.recvInt();
		assert(cmd == Protocol.COMMAND.IMAGE);
		byte[] img = con.recvImage();	
		
		System.out.println("Received " + img.length + " bytes.");
		jframe_show_jpeg("Primus Inter Pares ", img);
	}
	
	public void jframe_show_jpeg(String framename, byte[] data) {
		JFrame f = new JFrame(framename); 		
		ImageIcon img = new javax.swing.ImageIcon(data,"jpeg frame");
		f.getContentPane().add(new javax.swing.JLabel(img)); 				
		f.setSize(img.getIconWidth(),img.getIconHeight()); 
		f.setVisible(true); 
	}


}
