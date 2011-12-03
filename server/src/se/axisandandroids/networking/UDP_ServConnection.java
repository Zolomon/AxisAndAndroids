package se.axisandandroids.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import se.axisandandroids.server.CameraMonitor;
import se.lth.cs.fakecamera.Axis211A;


/**
 * Extends connection UDP image sending for server side use.
 * @author jg
 */
public class UDP_ServConnection extends Connection {

	private CameraMonitor  camera_monitor;
	private InetAddress	   clientAddress;
	private int 		   udp_port;		
	private DatagramSocket send_udp_socket;
	private DatagramPacket send_udp_packet;
	
	protected final byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];


	public UDP_ServConnection(String host, int tcp_port, CameraMonitor camera_monitor) {
		super(host, tcp_port);
		this.camera_monitor = camera_monitor;
		try {
			this.clientAddress = InetAddress.getByName(host);
			this.udp_port = tcp_port;		
			send_udp_socket = new DatagramSocket();		
			send_udp_packet = new DatagramPacket(jpeg, Axis211A.IMAGE_BUFFER_SIZE, clientAddress, udp_port);	
		} catch (UnknownHostException e) {
			e.printStackTrace();
			disconnect();
			System.err.println("Unknown host.");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			disconnect();
			System.err.println("IO except.");
			System.exit(1);
		}
	}

	public UDP_ServConnection(Socket tcpsocket, int tcp_port, CameraMonitor camera_monitor) {
		super(tcpsocket);				
		this.camera_monitor = camera_monitor;
		try {
			this.clientAddress = tcpsocket.getInetAddress();
			this.udp_port = tcp_port;		
			send_udp_socket = new DatagramSocket();		
			send_udp_packet = new DatagramPacket(jpeg, Axis211A.IMAGE_BUFFER_SIZE, clientAddress, udp_port);	
		} catch (IOException e) {
			e.printStackTrace();
			disconnect();
			System.err.println("IO except.");
			System.exit(1);
		}
	}

	public void disconnect() {
		System.out.println("Disconnecting in UDP_ServConnection");
		camera_monitor.setDisconnect(true);
		if (send_udp_socket != null) send_udp_socket.close();
		super.disconnect();		
	}

	public void sendImage(byte[] data) {		
		//System.out.println("Sending: " + data.length + " bytes, over port " + udp_port);
		synchronized (send_udp_socket) {
			send_udp_packet.setLength(data.length);
			send_udp_packet.setData(data, 0, data.length);
			try {
				send_udp_socket.send(send_udp_packet);
			} catch (IOException e) {
				e.printStackTrace();
				disconnect();
			}	
		}		
	}


	public void sendImage(byte[] data, int a, int b) {
		int len = b - a;		
		//System.out.println("Sending: " + len + " bytes, over port " + udp_port);
		synchronized (send_udp_socket) {
			send_udp_packet.setLength(len);
			send_udp_packet.setData(data, a, len);
			try {
				send_udp_socket.send(send_udp_packet);
			} catch (IOException e) {
				e.printStackTrace();
				disconnect();
			}	
		}
	}


	public int recvImage(byte[] b) {	
		return -1;
	}
	

}
