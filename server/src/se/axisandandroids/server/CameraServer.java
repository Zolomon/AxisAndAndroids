package se.axisandandroids.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CameraServer {

	private static int port;
	ServerSocket servSocket = null;

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

			System.out.printf("Serving client: %s", clientSocket
					.getInetAddress().toString());

			servClient(clientSocket);
		}
	}

	private void servClient(Socket clientSock) {
		// listen commands, fetch images, send images ???

	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out
					.println("Choose port 1024-65535 with syntax: CameraServer <port> ");
			System.exit(1);
		}

		port = Integer.parseInt(args[0]);
		CameraServer serv = new CameraServer(port);
		serv.listenForConnection();
	}

}
