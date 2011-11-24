package se.axisandandroids.fakecamserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//import se.axisandandroids.http.JPEGHTTPServerThread;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.server.CameraMonitor;
import se.axisandandroids.server.ServerReceiveThread;
import se.axisandandroids.server.ServerSendThread;
import se.lth.cs.fakecamera.*;

public class FakeCameraServer {

	private final static int default_port = 6000;
	private int port;
	private ServerSocket servSocket = null;
	private Connection con;
	private CameraMonitor cm;
	private FakeCameraThread ct;
	private ServerReceiveThread receiveThread;
	private ServerSendThread sendThread;	
	private Axis211A myCamera;
	private String host = "localhost";
	private MotionDetector md;


	public static void main(String[] args) {

		int defport = default_port;
		boolean http = false;
		boolean fake = false;

		for (int argc = 0; argc < args.length; ++argc) {
			if (args[argc].equals("-help")) {
				System.out.println("Usage: CameraServer [options] [port]");
				System.out.println("Options:");			
				System.out.println("\t-help  - Show help.");
				System.exit(0);
			} 
			else defport = Integer.parseInt(args[argc]);			
		}


		System.out.println("Big brother is watching you all, Axis and Androids...");

		FakeCameraServer serv = new FakeCameraServer(defport, http, fake);
		serv.listenForConnection();
	}

	public FakeCameraServer(int port, boolean http, boolean fake) {
		this.port = port;

		myCamera = new Axis211A();
		md = new MotionDetector();

		try {
			servSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.printf("Could not listen on port: %d", port);
			System.exit(1);
		}
		System.out.println("Camera Server up and running...");
	}

	private void listenForConnection() {

		System.out.printf("Listening on port: %d\n", port);

		while (true) {
			Socket clientSocket = null;
			try {
				clientSocket = servSocket.accept();
			} catch (IOException e) {
				System.out.printf("Accept failed: %d", port);
				System.exit(1);
			}

			System.out.printf("Serving client: %s\n", clientSocket
					.getInetAddress().toString());

			/* Handle the client some way !!! */ 

			// With a thread object if multi-client:
			// new ClientHandler(Connection client).start();

			// OR if only one client...

			servClient(clientSocket);						
		}
	}

	private void servClient(Socket clientSock) {
		con = new Connection(clientSock);		
		cm = new CameraMonitor();
		receiveThread = new ServerReceiveThread(con, cm);
		sendThread = new ServerSendThread(con);
		ct = new FakeCameraThread(cm, sendThread.mailbox, myCamera, md);
		receiveThread.start();
		sendThread.start();
		ct.start();
	}

}
