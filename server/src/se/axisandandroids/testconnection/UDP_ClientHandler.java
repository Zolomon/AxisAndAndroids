package se.axisandandroids.testconnection;

/* ---------------------------------------------------
 * Thread for handling ONE client given by socket at
 * construction.
 * 
 * --------------------------------------------------- */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import se.lth.cs.fakecamera.Axis211A;



/**
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class UDP_ClientHandler extends Thread {
	private Socket clientSocket;
	private Axis211A axis;
	private int tcp_port;
	private int udp_port;
	
	public UDP_ClientHandler(Socket clientSocket, int tcp_port, Axis211A axis) {
		super();
		this.clientSocket = clientSocket;
		this.axis = axis;
		this.tcp_port = tcp_port;
		this.udp_port = tcp_port + 1;
	}

	public void run() {
		try {

			// *** --- CHANGE HERE WHAT TO RUN --- ***

			servFakeCamUDP(clientSocket);		

			// *** ------------------------------- ***

			clientSocket.close();
		} catch (IOException e) {
			System.err.println("io-exception");
			System.exit(1);
		}		
	}
	
	public void servFakeCamUDP(Socket sock) throws IOException {
		if (! axis.connect()) {
			System.out.println("Failed to connect to camera!");
			System.exit(1);
		}	
		
		//Connection con = new Connection(sock);
		
		
		byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];

		InetAddress client 		= sock.getInetAddress();			
		System.out.println("Serving Client: " + client.getHostAddress() + " on port: " + udp_port);

		DatagramSocket udp_socket = new DatagramSocket();		
		DatagramPacket udp_packet = new DatagramPacket(jpeg, Axis211A.IMAGE_BUFFER_SIZE, client, udp_port);  				
		System.out.println("UDP socket created");
		
				
		System.out.println("Now serving images...");

		while (!interrupted()) {
			int len = axis.getJPEG(jpeg, 0);		
			udp_packet.setLength(len);
			udp_packet.setData(jpeg, 0, len);
			udp_socket.send(udp_packet);			
		}

		System.out.println("Done with this.");

		
		axis.close();		
	}
}
