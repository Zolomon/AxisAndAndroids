package se.axisandandroids.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import se.axisandandroids.http.JPEGHTTPServerThread;
import se.axisandandroids.networking.Connection;
import se.lth.cs.cameraproxy.Axis211A;
import se.lth.cs.cameraproxy.MotionDetector;

public class CameraServer {

	private final static int default_port = 6000;
	private int listenPort;
	private ServerSocket servSocket = null;
	private Connection con;
	private CameraMonitor cm;
	private CameraThread ct;
	private ServerReceiveThread receiveThread;
	private ServerSendThread sendThread;
	private Axis211A myCamera;
	private JPEGHTTPServerThread httpServer;
	private MotionDetector md;
	private String host = "argus-8.student.lth.se";
	private int cameraPort = 4321;

	public static void main(String[] args) {

		int listenPort = default_port;
		int camport = 4321;
		String camhost = "argus-8.student.lth.se";
		boolean http = false;

		for (int argc = 0; argc < args.length; ++argc) {
			if (args[argc].equals("-http"))
				http = true;
			else if (args[argc].equals("-help")) {
				System.out.println("Usage: CameraServer [options] [port]");
				System.out.println("Options:");
				System.out
						.println("\t-http  - Run tiny http server as well as camera server.");
				System.out.println("\t-help  - Show help.");
				System.exit(0);
			} else if (args[argc].equals("-camera")) {
				camhost = args[++argc];
				camport = Integer.parseInt(args[++argc]);
			}
			else listenPort = Integer.parseInt(args[argc]);
		}

		System.out
				.println("Big brother is watching you all, Axis and Androids...");

		CameraServer serv = new CameraServer(listenPort, camhost, camport, http);
		serv.listenForConnection();
	}

	public CameraServer(int listenPort, String camhost, int camport, boolean http) {
		this.listenPort = listenPort;
		
		this.host = camhost;
		this.cameraPort = camport;

		myCamera = new Axis211A(host, cameraPort);
		md = new MotionDetector(host, cameraPort);

		if (http) {
			httpServer = new JPEGHTTPServerThread(8080, myCamera);
			httpServer.start();
		}

		try {
			servSocket = new ServerSocket(listenPort);
		} catch (IOException e) {
			System.out.printf("Could not listen on port: %d", listenPort);
			System.exit(1);
		}
		System.out.println("Camera Server up and running...");
	}

	private void listenForConnection() {

		System.out.printf("Listening on port: %d\n", listenPort);

		while (true) {
			Socket clientSocket = null;
			
			try {
				clientSocket = servSocket.accept();
			} catch (IOException e) {
				System.out.printf("Accept failed: %d", listenPort);
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
		ct = new CameraThread(cm, sendThread.mailbox, myCamera, md);
		receiveThread.start();
		sendThread.start();
		ct.start();
	}

}
