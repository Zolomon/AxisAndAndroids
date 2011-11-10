package se.axisandandroids.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class CameraServer {

	static int default_port = 6000;
	
	int port;
	ServerSocket servSocket = null;	

	
	public static void main(String[] args) {
		System.out.println("Big brother is watching you all: Axis and Androids...");
	
		int port = default_port;
		if (args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}		
		
		CameraServer serv = new CameraServer(port);
		serv.listenForConnection();	
	}
	
	
	public CameraServer(int port) {
		this.port = port;
		try {
			servSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.printf("Could not listen on port: %d", port);
			System.exit(1);
		}
		System.out.println("Camera Server up and running...");
	}
	
	
	public void listenForConnection() {
		
		System.out.printf("Listening on port: %d", port);
		
		while (true) {
			Socket clientSocket = null;
			try {
				clientSocket = servSocket.accept();
			} catch (IOException e) {
				System.out.printf("Accept failed: %d", port);
				System.exit(1);
			}
			
			System.out.printf("Serving client: %s", clientSocket.getInetAddress().toString());
			
			
			// * What was our plan here?
			// Create some Connection object with socket,
			// then some ClientHandler with the Connection object.
						
			// Handle the client some way!!! e.g. with a thread object:
			// new ClientHandler(Connection client).start();
		
			
			// OR if only one client...
			servClient(clientSocket);
		}
	}

	private void servClient(Socket clientSock) {
		// listen commands, fetch images, send images ???

	}

	
}
