package se.axisandandroids.testconnection;

/* ---------------------------------------------------
 * Thread for handling ONE client given by socket at
 * construction.
 * 
 * --------------------------------------------------- */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.Protocol;

public class ClientHandler extends Thread {
	Socket clientSocket;

	public ClientHandler(Socket clientSocket) {
		super();
		this.clientSocket = clientSocket;
	}

	public void run() {
		try {
			//servClient(clientSocket);

			servConnectionTest(clientSocket);
		} catch (IOException e) {
			System.err.println("io-exception");
			System.exit(1);
		}
	}
	

	public void servClient(Socket clientSocket) throws IOException {	
		System.out.println("Serving client: " + clientSocket.getInetAddress().toString());

		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));

		String inputLine, outputLine;
		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
			outputLine = "[Ether] " + inputLine;
			out.println(outputLine);
		}

		System.out.println("Client Disconnected: "
				+ clientSocket.getInetAddress().toString());
	}


	
	
	public void servConnectionTest(Socket sock) throws IOException {
		Connection con = new Connection(sock);

		// Test recvInt()
		System.out.println("\n** Receiving int...");
		int nbr = con.recvInt();
		System.out.printf("Got int: %d\n", nbr);

		// Test sendInt()
		System.out.println("\n** Sending int...");
		nbr = 123123;
		System.out.printf("Sending int: %d\n", nbr);
		con.sendInt(nbr);

		// Test recvSyncMode
		System.out.println("\n** Received SyncMode...");
		int cmd = con.recvInt();
		int mode = con.recvSyncMode();
		System.out.printf("Command %d Mode %d\n", cmd, mode);
		assert(cmd == Protocol.COMMAND.SYNC_MODE);
		assert(mode == Protocol.SYNC_MODE.AUTO);

		// Test recvDisplayMode
		System.out.println("\n** Received DisplayMode...");
		cmd = con.recvInt();
		mode = con.recvDisplayMode();
		System.out.printf("Command %d Mode %d\n", cmd, mode);
		assert(cmd == Protocol.COMMAND.DISP_MODE);
		assert(mode == Protocol.DISP_MODE.AUTO);


		// Test sendImage
		System.out.println("\n** Sending Image...");
		byte[] c = { 12,43,34,120,21,32,100,34 };				
		con.sendImage(c);

		// Test recvImage
		System.out.println("\n** Receiving Image...");
		cmd = con.recvInt();
		assert(cmd == Protocol.COMMAND.IMAGE);
		System.out.println("Command: " + cmd);			
		byte[] b = con.recvImage();		
		System.out.printf("Length: %d\n", b.length);
		for (int i = 0; i < b.length; ++i) {
			System.out.printf("%d ", b[i]);
			assert(b[i] == c[i]);
		}
		System.out.println();

	}

}
