package se.axisandandroids.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class Connection {
	
	private Socket sock;		
	private InputStream is;		// input and output should be independent
	private OutputStream os;
	private BufferedReader in;
	private PrintWriter out;
			
	public Connection(Socket sock) {
		this.sock = sock;		
		connect();
	}
	
	public void connect(Socket sock) {
		this.sock = sock;
		connect();
	}
	
	private void connect() {
		try {
			is = sock.getInputStream();
			os = sock.getOutputStream();
			out = new PrintWriter(os, true);
			in = new BufferedReader(new InputStreamReader(is));
//			ObjectInputStream ois = new ObjectInputStream(is);
			
		} catch (IOException e) {
			System.err.println("IO-error");
			System.exit(1);
		}		
		System.out.printf("New Connection: %s\n", sock.getInetAddress().toString());
	}
	
	
	public void disconnect() throws IOException {
		System.out.printf("Disconnected: %s\n", sock.getInetAddress().toString());
		// Disconnect Message ???
		sock.close();
		sock = null; // etc...
	}
		
				
	public void sendImage(byte[] data) {		
		sendInt(Protocol.COMMAND.IMAGE);
		sendInt(data.length);		
		out.println(new String(data));

		/*
		try {
			os.write(data, 0, data.length);		
			os.flush();
		} catch (IOException e) {
			System.err.println("IO-error");
			System.exit(1);
		}		
		*/			
	}
	
	public byte[] recvImage() throws IOException {				
		int len = recvInt();				
		byte[] b = null;	
		try {
			//int bytes_read = is.read(b, 0, len);		
			b = in.readLine().getBytes();			
		} catch (IOException e) {
			System.err.println("IO-error");
			System.exit(1);
		}										
		return b;
	}
	
	
	public void sendDisplayMode(int disp_mode) {
		sendInt(Protocol.COMMAND.DISP_MODE);
		sendInt(disp_mode);
	}
	
	public int recvDisplayMode() throws IOException { 		
		return recvInt();
	}
	
	public void sendSyncMode(int sync_mode) {
		sendInt(Protocol.COMMAND.SYNC_MODE);
		sendInt(sync_mode);
	}
	
	public int recvSyncMode() throws IOException { 
		return recvInt();
	}
				
	public void sendInt(int nbr) {				
		char[] b = new char[4];		
		b[0] = (char) ((nbr >> 24) & 0xFF);
		b[1] = (char) ((nbr >> 16) & 0xFF);
		b[2] = (char) ((nbr >> 8)  & 0xFF);
		b[3] = (char) ( nbr 	   & 0xFF);
		out.print(b);
		out.flush();
				
		/*
		out.write((nbr >> 24) & 0xFF);
		out.write((nbr >> 16) & 0xFF);
		out.write((nbr >> 8)  & 0xFF);
		out.write( nbr 	      & 0xFF);
		out.flush();
		*/
	}
	
	public int recvInt() throws IOException {		
		char[] b = new char[4];
		int status = in.read(b, 0, b.length);
		if (status == -1) {
			System.err.println("IO-error - reached EOF.");
			System.exit(1);
		}
		return b[0] << 24 | b[1] << 16 | b[2] << 8 | b[3];
		
		/*
		int b0 = in.read();
		int b1 = in.read();
		int b2 = in.read();
		int b3 = in.read();
		return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
		*/
	}		
	
}
