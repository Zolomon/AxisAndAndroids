package se.axisandandroids.networking;

import java.net.Socket;


public class Connection {
	
	private Socket clientSock;
	
	
	public Connection(Socket clientSock) {
		this.clientSock = clientSock;
	}
	
	
	public void sendImage(byte[] data) {
				
	}
	
	public byte[] recvImage() {
		
		return null;
	}
	

}
