package se.axisandandroids.client.service.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.networking.Connection;
import se.lth.cs.fakecamera.Axis211A;


/**
 * Extends Connection with UDP image fetching for client side use.
 * @author jg
 */
public class UDP_ClientConnection extends Connection {

	private DisplayMonitor disp_monitor;
	private int 		   udp_port;	
	private DatagramSocket recv_udp_socket;
	private DatagramPacket recv_udp_packet;
	protected final byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];


	public UDP_ClientConnection(String host, int tcp_port, DisplayMonitor disp_monitor) {
		super(host, tcp_port);	
		try {
			this.disp_monitor = disp_monitor;
			this.udp_port = tcp_port;				
			recv_udp_socket = new DatagramSocket(udp_port);
			recv_udp_packet = new DatagramPacket(jpeg, Axis211A.IMAGE_BUFFER_SIZE);
		} catch (SocketException e) {
			e.printStackTrace();
			disconnect();
		}		
	}

	public UDP_ClientConnection(Socket tcpsocket, int tcp_port, DisplayMonitor disp_monitor) {
		super(tcpsocket);
		try {
			this.disp_monitor = disp_monitor;
			this.udp_port = tcp_port;		
			recv_udp_socket = new DatagramSocket(udp_port);
			recv_udp_packet = new DatagramPacket(jpeg, Axis211A.IMAGE_BUFFER_SIZE);
		} catch (SocketException e) {
			e.printStackTrace();
			disconnect();
		}		
	}

	public void disconnect() {
		if (recv_udp_socket != null)
			recv_udp_socket.close();
		disp_monitor.setDisconnect(true);
		super.disconnect();		
	}

	public int recvImage(byte[] b) {	
		synchronized (recv_udp_socket) {
			recv_udp_packet.setLength(Axis211A.IMAGE_BUFFER_SIZE);
			recv_udp_packet.setData(b, 0, b.length);
			try {
				recv_udp_socket.receive(recv_udp_packet);
			} catch (IOException e) {
				disconnect();
				e.printStackTrace();
			} 
			int len = recv_udp_packet.getLength();
			//System.out.println("Received " + len + " bytes.");
			return len;
		}
	}

	public void sendImage(byte[] data) {}	
	public void sendImage(byte[] data, int a, int b) {}

}
