package se.axisandandroids.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import se.lth.cs.fakecamera.Axis211A;

public class UDP_Connection extends Connection {

	private InetAddress	   clientAddress;
	private int 		   udp_port;
		
	private DatagramSocket send_udp_socket;
	private DatagramPacket send_udp_packet;
	
	private DatagramSocket recv_udp_socket;
	private DatagramPacket recv_udp_packet;



	public UDP_Connection(String host, int port) throws UnknownHostException, IOException {
		super(host, port);
		this.clientAddress = InetAddress.getByName(host);
		this.udp_port = port + 1;
		
		send_udp_socket = new DatagramSocket();		
		send_udp_packet = new DatagramPacket(new byte[Axis211A.IMAGE_BUFFER_SIZE], 
													Axis211A.IMAGE_BUFFER_SIZE, 
													clientAddress, udp_port);
		
		recv_udp_socket = new DatagramSocket(udp_port);		
		recv_udp_packet = new DatagramPacket(new byte[Axis211A.IMAGE_BUFFER_SIZE], 
													Axis211A.IMAGE_BUFFER_SIZE);

	}

	public UDP_Connection(Socket tcpsocket, int tcp_port) throws UnknownHostException, IOException {
		super(tcpsocket);
				
		this.clientAddress = tcpsocket.getInetAddress();
		this.udp_port = tcp_port + 1;
		
		send_udp_socket = new DatagramSocket();		
		send_udp_packet = new DatagramPacket(new byte[Axis211A.IMAGE_BUFFER_SIZE], 
													Axis211A.IMAGE_BUFFER_SIZE, 
													clientAddress, udp_port);
		
		recv_udp_socket = new DatagramSocket(udp_port);		
		recv_udp_packet = new DatagramPacket(new byte[Axis211A.IMAGE_BUFFER_SIZE], 
													Axis211A.IMAGE_BUFFER_SIZE);

	}


	public void sendImage(byte[] data) throws IOException {		
		synchronized (send_udp_socket) {
			send_udp_packet.setLength(data.length);
			send_udp_packet.setData(data, 0, data.length);
			send_udp_socket.send(send_udp_packet);	
		}		
	}

	
	public void sendImage(byte[] data, int a, int b) throws IOException {
		synchronized (send_udp_socket) {
			int len = b - a;		
			send_udp_packet.setLength(len);
			send_udp_packet.setData(data, a, len);
			send_udp_socket.send(send_udp_packet);	
		}
	}


	public int recvImage(byte[] b) throws IOException {	
		synchronized (recv_udp_socket) {
			recv_udp_packet.setLength(Axis211A.IMAGE_BUFFER_SIZE);
			recv_udp_packet.setData(b, 0, b.length);
			recv_udp_socket.receive(recv_udp_packet); 
			int len = recv_udp_packet.getLength();
			System.out.println("Received " + len + " bytes.");
			return len;
		}
	}


}
