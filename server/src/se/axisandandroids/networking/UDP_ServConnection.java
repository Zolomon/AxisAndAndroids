package se.axisandandroids.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import se.lth.cs.fakecamera.Axis211A;

public class UDP_ServConnection extends Connection {

	private InetAddress	   clientAddress;
	private int 		   udp_port;		
	private DatagramSocket send_udp_socket;
	private DatagramPacket send_udp_packet;
	protected final byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];
	

	public UDP_ServConnection(String host, int port) throws UnknownHostException, IOException {
		super(host, port);
		this.clientAddress = InetAddress.getByName(host);
		this.udp_port = port + 1;
		
		send_udp_socket = new DatagramSocket();		
		send_udp_packet = new DatagramPacket(jpeg, Axis211A.IMAGE_BUFFER_SIZE, 
													clientAddress, udp_port);		
	}

	public UDP_ServConnection(Socket tcpsocket, int tcp_port) throws UnknownHostException, IOException {
		super(tcpsocket);
				
		this.clientAddress = tcpsocket.getInetAddress();
		this.udp_port = tcp_port + 1;
		
		send_udp_socket = new DatagramSocket();		
		send_udp_packet = new DatagramPacket(jpeg, Axis211A.IMAGE_BUFFER_SIZE, 
				clientAddress, udp_port);		
	}


	public void sendImage(byte[] data) throws IOException {		
		System.out.println("Sending image");
		synchronized (send_udp_socket) {
			send_udp_packet.setLength(data.length);
			send_udp_packet.setData(data, 0, data.length);
			send_udp_socket.send(send_udp_packet);	
		}		
	}

	
	public void sendImage(byte[] data, int a, int b) throws IOException {
		int len = b - a;		
		System.out.println("Sending: " + len + " bytes, over port " + udp_port);
		synchronized (send_udp_socket) {
			send_udp_packet.setLength(len);
			send_udp_packet.setData(data, a, len);
			send_udp_socket.send(send_udp_packet);	
		}
	}


	public int recvImage(byte[] b) throws IOException {	
		return -1;
	}


}
