package se.axisandandroids.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import se.lth.cs.fakecamera.Axis211A;

public class UDP_ClientConnection extends Connection {

	private int 		   udp_port;	
	private DatagramSocket recv_udp_socket;
	private DatagramPacket recv_udp_packet;
	protected final byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];


	public UDP_ClientConnection(String host, int tcp_port) throws UnknownHostException, IOException {
		super(host, tcp_port);
		this.udp_port = 6001; //tcp_port + 1; // ---------------------------------> had to hard code this			
		recv_udp_socket = new DatagramSocket(udp_port);		
		recv_udp_packet = new DatagramPacket(jpeg, Axis211A.IMAGE_BUFFER_SIZE);
	}

	public UDP_ClientConnection(Socket tcpsocket, int tcp_port) throws UnknownHostException, IOException {
		super(tcpsocket);				
		this.udp_port = 6001; //tcp_port + 1; // ---------------------------------> had to hard code this
		recv_udp_socket = new DatagramSocket(udp_port);		
		recv_udp_packet = new DatagramPacket(jpeg, Axis211A.IMAGE_BUFFER_SIZE);
	}
		
	public int recvImage(byte[] b) throws IOException {	
		synchronized (recv_udp_socket) {
			recv_udp_packet.setLength(Axis211A.IMAGE_BUFFER_SIZE);
			recv_udp_packet.setData(b, 0, b.length);
			recv_udp_socket.receive(recv_udp_packet); 
			int len = recv_udp_packet.getLength();
			//System.out.println("Received " + len + " bytes.");
			return len;
		}
	}
	
	public void sendImage(byte[] data) throws IOException {}	
	public void sendImage(byte[] data, int a, int b) throws IOException {}

}
