package se.axisandandroids.testconnection;

/* ---------------------------------------------------
 * Simple TCP server "ditto"-ing the input from the
 * clients. Serving multiple clients.
 * 
 * Based on this tutorial:
 * 		http://download.oracle.com/javase/tutorial/...
 * 					networking/sockets/clientServer.html
 * --------------------------------------------------- */


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import se.lth.cs.fakecamera.Axis211A;


/**
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class TCP_Server {
	
	private Axis211A axis;
	private ServerSocket servSocket = null;
	private final static int default_port = 5555;
	private int port;
	

	public TCP_Server(int port) {
		this.port = port;
		try {
			servSocket = new ServerSocket(port);
			axis = new Axis211A();
		} catch (IOException e) {
			System.out.printf("Could not listen on port: %d\n", port);
			System.exit(1);
		}
	}

	private void listenForConnection() {
		while (true) {
			Socket clientSocket = null;
			try {
				clientSocket = servSocket.accept();
			} catch (IOException e) {
				System.out.printf("Accept failed: %d\n", port);
				System.exit(1);
			}
			new ClientHandler(clientSocket, axis).start();			
		}
	}

	public static void main(String[] args) {
		int port = default_port;
		if (args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}		
		TCP_Server serv = new TCP_Server(port);
		serv.listenForConnection();
	}


}
