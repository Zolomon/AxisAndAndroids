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
	private int mPort;
	private int mNumberOfClients = 0;
	private ServerSocket mServerSocket = null;
	private Connection mConnnection;
	private CameraMonitor mCameraMonitor;
	private FakeCameraThread mFakeCameraThread;
	private ServerReceiveThread mServerReceiveThread;
	private ServerSendThread mSendThread;
	private Axis211A mAxisCamera;
	private MotionDetector mMotionDetector;

	public static void main(String[] args) {
		int port = default_port;

		for (int argc = 0; argc < args.length; ++argc) {
			if (args[argc].equals("-help")) {
				System.out.println("Usage: CameraServer [options] [port]");
				System.out.println("Options:");
				System.out.println("\t-help  - Show help.");
				System.exit(0);
			} else
				port = Integer.parseInt(args[argc]);
		}

		System.out
				.println("Big brother is watching you all, Axis and Androids...");
		FakeCameraServer serv = new FakeCameraServer(port);
		serv.listenForConnection();
	}

	public FakeCameraServer(int port) {
		this.mPort = port;

		mAxisCamera = new Axis211A();
		mMotionDetector = new MotionDetector();

		try {
			mServerSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.printf("Could not listen on port: %d", port);
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Camera Server up and running...");
	}

	private void listenForConnection() {

		System.out.printf("Listening on port: %d\n", mPort);

		while (true) {
			Socket clientSocket = null;
			System.out.println("Ready to accept...");
			try {
				clientSocket = mServerSocket.accept();
			} catch (IOException e) {
				System.out.printf("Accept failed: %d\n", mPort);
				System.exit(1);
			}

			System.out.printf("Serving client %d: %s\n", ++mNumberOfClients,
					clientSocket.getInetAddress().toString());

			/* Handle the client some way !!! */
			servClient(clientSocket);
		}
	}

	private void servClient(Socket clientSock) {
		mConnnection = new Connection(clientSock);
		mCameraMonitor = new CameraMonitor();
		mServerReceiveThread = new ServerReceiveThread(mConnnection,
				mCameraMonitor);
		mSendThread = new ServerSendThread(mConnnection);
		mFakeCameraThread = new FakeCameraThread(mCameraMonitor,
				mSendThread.mailbox, mSendThread.frame_buffer, mAxisCamera,
				mMotionDetector);
		mServerReceiveThread.start();
		mSendThread.start();
		mFakeCameraThread.start();
	}

}
