package se.axisandandroids.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import se.lth.cs.fakecamera.Axis211A;


public class Connection {
	
	private Socket sock;	
	private InputStream input;		// input and output should be independent
	private OutputStream output;
	
	
	public static enum COMMANDS { IMAGE, SYNC_MODE, DISP_MODE, CONNECTED, END_MSG }
	public static enum SYNC_MODE { AUTO, SYNC, ASYNC }
	public static enum DISP_MODE { AUTO, IDLE, MOVIE }
	
		
	public Connection(Socket sock) {
		this.sock = sock;
		try {
			input = sock.getInputStream();
			output = sock.getOutputStream();
		} catch (IOException e) {
			System.err.println("IO-error");
			System.exit(1);
		}
		connect();
	}
	
		
	public void sendImage(byte[] data) {		
		try {
			output.write(data, 0, data.length);
		} catch (IOException e) {
			System.err.println("IO-error");
			System.exit(1);
		}			
	}
	
	public byte[] recvImage() {
		
		// Read to a buffer array instead ???
		
		byte[] b = new byte[Axis211A.IMAGE_BUFFER_SIZE];
		int len;
		
		try {
			len = input.read(b, 0, Axis211A.IMAGE_BUFFER_SIZE);			
		} catch (IOException e) {
			System.err.println("IO-error");
			System.exit(1);
		}			
		
		return null; // note
	}
	
	
	public void sendDisplayMode(int disp_mode) {
		sendInt(disp_mode); // Sanity Check ?
	}
	
	public int recvDisplayMode() { 		
		return recvInt(); // Sanity Check ?
	}
	
	public void sendSyncMode(int sync_mode) {
		sendInt(sync_mode); // Sanity Check ?
	}
	
	public int recvSyncMode() { 
		return recvInt();  // Sanity Check ?
	}
	
	public void connect() {
		System.out.printf("New Connection: %s", sock.getInetAddress().toString());
	}
	
	public void disconnect() {
		System.out.printf("Disconnected: %s", sock.getInetAddress().toString());
		sock = null; // etc...
	}
	
	
	private void sendInt(int nbr) {
		
	}
	
	private int recvInt() { 
		return 0; 
	}
	
	

}
