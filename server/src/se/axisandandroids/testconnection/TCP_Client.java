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


import se.axisandandroids.networking.Connection;


public class TCP_Client {
	Socket socket;
	InetAddress host;
	int port;

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
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));		
		String userInput;		
		while ((userInput = stdIn.readLine()) != null) {
			out.println(userInput);
			System.out.println(in.readLine());
		}
		stdIn.close();
	}
	
	
	
	public void connection_test() throws IOException {		
		System.out.println("Connection Test");

		Connection con = new Connection(socket);

		// Test sendInt()
		int nbr = 983745;
		System.out.printf("Sending int: %d\n", nbr);
		con.sendInt(nbr);
		
		// Test recvInt()
		nbr = con.recvInt();
		System.out.printf("Got int: %d\n", nbr);		
	}
	


	public static void main(String[] args) {
		InetAddress addr = null;
		int port = 5000;

		try {
			addr = InetAddress.getByName("localhost");
			if (args.length >= 1) {
				addr = InetAddress.getByName(args[0]);
			} 
		} catch(UnknownHostException e) {
			System.err.println("Unknown host.");
			System.exit(1);
		}

		if (args.length >= 2) {
			port = Integer.parseInt( args[1] );
		}

		TCP_Client tcpclient = new TCP_Client(addr, port);
		
		try {
			//tcpclient.userinput_echo();
			
			tcpclient.connection_test();
			tcpclient.disconnect();
		} catch (IOException e) {
			System.err.println("io-exception");
			System.exit(1);
		}		
	}
	
}
