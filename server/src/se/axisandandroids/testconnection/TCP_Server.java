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

public class TCP_Server {

	ServerSocket servSocket = null;
	int port;

	public TCP_Server(int port) {
		this.port = port;
		try {
			servSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.printf("Could not listen on port: %d\n", port);
			System.exit(1);
		}
	}

	public void listenForConnection() {
		while (true) {
			Socket clientSocket = null;
			try {
				clientSocket = servSocket.accept();
			} catch (IOException e) {
				System.out.printf("Accept failed: %d\n", port);
				System.exit(1);
			}
			new ClientHandler(clientSocket).start();			
		}
	}

	public static void main(String[] args) {
		int port = 6001;
		if (args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}		
		TCP_Server serv = new TCP_Server(port);
		serv.listenForConnection();
	}


}
