package se.axisandandroids.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;


public class Connection {
	private String host;
	private int port;
	private Socket sock;		

	// Input and output should be independent!
	// Design => one sender and one receiver => thread safe in that regard
	// Alternative is to wrap to private monitors or synchronize on the streams.

	private InputStream is;									
	private OutputStream os;


	public Connection(Socket sock) {
		this.sock = sock;	

		try {
			//sock.setSoTimeout(10000);
			sock.setTcpNoDelay(true);
			//sock.setSendBufferSize(1024);
			//sock.setReceiveBufferSize(1024);
		} catch (SocketException e) {
			System.err.println("Argh! socket slained without delay.");
			System.exit(1);
		}
		connect();
	}

	public String getHost() { return host; }
	public int getPort() { return port; }

	public void connect(Socket sock) { 
		// Potentially Dangerous, synchronize use of sock? 
		this.sock = sock;
		connect();
	}

	private void connect() {
		try {
			is = sock.getInputStream();
			os = sock.getOutputStream();
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


	public void sendImage(byte[] data) throws IOException {		
		sendInt(Protocol.COMMAND.IMAGE);
		sendInt(data.length);		

		//assert(data.length <= Axis211A.IMAGE_BUFFER_SIZE);

		os.write(data, 0, data.length);	 // EOF how ???
		os.flush();
	}

	public void sendImage(byte[] data, int a, int b) throws IOException {
		int len = b - a;

		//assert(data.length <= Axis211A.IMAGE_BUFFER_SIZE);
		//assert(a + len - 1 < data.length);

		sendInt(Protocol.COMMAND.IMAGE);
		sendInt(len);	

		os.write(data, a, len); // EOF how ???						
		os.flush();
	}

	public int recvImage(byte[] b) throws IOException {				
		int len = recvInt();				
		int status = 0;
		int bytes_read = 0;

		try {					
			while (bytes_read < len) {
				status = is.read(b, bytes_read, len - bytes_read);
				/* 	1) Blocking until data available. 
			 	2) -1 if EOF. 
			 	3) 0 if nothing read.			 */
				if (status > 0) {
					bytes_read += status;		
				}
			}
		} catch (IOException e) {
			System.err.println("IO-error");
			System.exit(1);
		}		
		return bytes_read;
	}

	public void sendDisplayMode(int disp_mode) throws IOException {
		sendInt(Protocol.COMMAND.DISP_MODE);
		sendInt(disp_mode);
	}

	public int recvDisplayMode() throws IOException { 		
		return recvInt();
	}

	public void sendSyncMode(int sync_mode) throws IOException {
		sendInt(Protocol.COMMAND.SYNC_MODE);
		sendInt(sync_mode);
	}

	public int recvSyncMode() throws IOException { 
		return recvInt();
	}


	private byte[] sendintbuffer = new byte[4];

	public void sendInt(int nbr) throws IOException {
		sendintbuffer[0] = (byte) ( (nbr & 0xff000000) >> 24 );
		sendintbuffer[1] = (byte) ( (nbr & 0x00ff0000) >> 16 );
		sendintbuffer[2] = (byte) ( (nbr & 0x0000ff00) >> 8	 );
		sendintbuffer[3] = (byte) ( (nbr & 0x000000ff) 		 );
		os.write(sendintbuffer, 0, sendintbuffer.length);
		os.flush();

		/*
		os.write( (nbr & 0xff000000) >> 24 	);
		os.write( (nbr & 0x00ff0000) >> 16 	);
		os.write( (nbr & 0x0000ff00) >> 8	);
		os.write( (nbr & 0x000000ff) 		);
		os.flush();
		 */
	}


	private byte[] readintbuffer = new byte[4]; 

	public int recvInt() throws IOException {	
		int status = 0;
		int bytes_read = 0;

		while(bytes_read < 4) {
			status = is.read(readintbuffer, bytes_read, 4 - bytes_read);
			/* 	1) Blocking until data available. 
			 	2) -1 if EOF. 
			 	3) 0 if nothing read.			 */
			if (status > 0) {
				bytes_read += status;		
			}		
		} 

		return ( ( (int)readintbuffer[0]) << 24 ) & 0xff000000 | 
				( ( (int)readintbuffer[1]) << 16 ) & 0x00ff0000 | 
				( ( (int)readintbuffer[2]) << 8  ) & 0x0000ff00 | 
				(   (int)readintbuffer[3]		  & 0x000000ff ); 

		/*
		// Non-Blocking, return -1 on fail...
		int b0 = is.read();
		int b1 = is.read();
		int b2 = is.read();
		int b3 = is.read();
		return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
		 */			
	}		
}
