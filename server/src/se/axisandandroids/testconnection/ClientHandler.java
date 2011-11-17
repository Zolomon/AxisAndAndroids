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
			
			//servConnectionTest(clientSocket);
			
			servTestSendImage(clientSocket); 
		} catch (IOException e) {
			System.err.println("io-exception");
			System.exit(1);
		}
	}

	public void servConnectionTest(Socket sock) throws IOException {
		Connection con = new Connection(sock);

		// Test recvInt()
		int nbr = con.recvInt();
		System.out.printf("Got int: %d\n", nbr);

		// Test sendInt()
		nbr = 123123;
		System.out.printf("Sending int: %d\n", nbr);
		con.sendInt(nbr);
		
		// test recvSyncMode
		int cmd = con.recvInt();
		int mode = con.recvSyncMode();
		System.out.printf("Command %d Mode %d\n", cmd, mode);
		assert(cmd == Protocol.COMMAND.SYNC_MODE);
		assert(mode == Protocol.SYNC_MODE.AUTO);

		// test recvDisplayMode
		cmd = con.recvInt();
		mode = con.recvDisplayMode();
		System.out.printf("Command %d Mode %d\n", cmd, mode);
		assert(cmd == Protocol.COMMAND.DISP_MODE);
		assert(mode == Protocol.DISP_MODE.AUTO);

		// send byte array / send image
		byte[] b = { 12,43,34,120,21,32,100,34 };
		
		con.sendImage(b);						
	}
	
	public void servTestSendImage(Socket sock) throws IOException {
		Connection con = new Connection(sock);		

		// send byte array / send image
		byte[] b = { 12,43,34,120,21,32,100,34 };		
		
		con.sendImage(b);					
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

}
