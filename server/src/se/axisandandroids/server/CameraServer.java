package se.axisandandroids.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import se.axisandandroids.networking.Connection;

public class CameraServer {

	private final static int default_port = 6000;
	private int port;
	private ServerSocket servSocket = null;
	private Connection con;
	private CameraMonitor cm;
	private CameraThread ct;
	private ServerReceiveThread receiveThread;
	private ServerSendThread sendThread;
	
	public static void main(String[] args) {
		System.out
				.println("Big brother is watching you all: Axis and Androids...");

		int defport = default_port;
		if (args.length >= 1) {
			defport = Integer.parseInt(args[0]);
		}

		CameraServer serv = new CameraServer(defport);
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

	private void listenForConnection() {

		System.out.printf("Listening on port: %d", port);

		while (true) {
			Socket clientSocket = null;
			try {
				clientSocket = servSocket.accept();
			} catch (IOException e) {
				System.out.printf("Accept failed: %d", port);
				System.exit(1);
			}

			System.out.printf("Serving client: %s", clientSocket
					.getInetAddress().toString());

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
		con = new Connection(clientSock);
		cm = new CameraMonitor();
		receiveThread = new ServerReceiveThread(con, cm);
		sendThread= new ServerSendThread(con);
		ct = new CameraThread(cm, sendThread.mailbox);

	}

}
