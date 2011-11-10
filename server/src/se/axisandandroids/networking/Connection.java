package se.axisandandroids.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class Connection {
	
	private Socket sock;
	private InputStream input;
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
	}
	
		
	public void sendImage(byte[] data) {
			
	}
	
	public byte[] recvImage() {		
		
		return null;
	}
	
	
	public void sendDisplayMode() {
		
	}
	
	public int recvDisplayMode() { 
		
		return 0; 
	}
	
	public void sendSyncMode() {
		
	}
	
	public int recvSyncMode() { 
		return 0; 
	}
	
	public void connect() {
		
	}
	
	public void disconnect() {
		
	}
	
	

}
